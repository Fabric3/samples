/*
 * Copyright (c) 2010 Metaform Systems
 *
 * See the NOTICE file distributed with this work for information
 * regarding copyright ownership.  This file is licensed
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.fabric3.samples.bigbank.loan.request;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.oasisopen.sca.annotation.Context;
import org.oasisopen.sca.annotation.ManagedTransaction;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Service;

import org.fabric3.api.Fabric3RequestContext;
import org.fabric3.api.annotation.Consumer;
import org.fabric3.api.annotation.Producer;
import org.fabric3.api.annotation.monitor.Monitor;
import org.fabric3.samples.bigbank.api.channel.LoanChannel;
import org.fabric3.samples.bigbank.api.event.ApplicationReady;
import org.fabric3.samples.bigbank.api.event.ApplicationReceived;
import org.fabric3.samples.bigbank.api.event.ApplicationRejected;
import org.fabric3.samples.bigbank.api.event.RiskAssessmentComplete;
import org.fabric3.samples.bigbank.api.loan.LoanException;
import org.fabric3.samples.bigbank.api.loan.LoanService;
import org.fabric3.samples.bigbank.api.message.LoanRequest;
import org.fabric3.samples.bigbank.loan.domain.LoanRecord;
import org.fabric3.samples.bigbank.loan.domain.PropertyInfo;
import org.fabric3.samples.bigbank.loan.domain.TermInfo;
import org.fabric3.samples.bigbank.services.credit.CreditScore;
import org.fabric3.samples.bigbank.services.credit.CreditService;
import org.fabric3.samples.bigbank.services.pricing.PriceResponse;
import org.fabric3.samples.bigbank.services.pricing.PricingOption;
import org.fabric3.samples.bigbank.services.pricing.PricingRequest;
import org.fabric3.samples.bigbank.services.pricing.PricingService;
import org.fabric3.samples.bigbank.services.pricing.PricingServiceCallback;
import org.fabric3.samples.bigbank.services.risk.RiskAssessmentService;
import org.fabric3.samples.bigbank.services.risk.RiskRequest;
import org.fabric3.samples.bigbank.services.risk.RiskResponse;

/**
 * Default implementation of the RequestCoordinator service.
 *
 * @version $Revision$ $Date$
 */
@Service(names = {RequestCoordinator.class, PricingServiceCallback.class})
@ManagedTransaction
public class RequestCoordinatorImpl implements RequestCoordinator, PricingServiceCallback {
    private CreditService creditService;
    private RiskAssessmentService riskService;
    private PricingService pricingService;
    private LoanChannel loanChannel;
    private RequestMonitor monitor;
    private EntityManager em;
    private Fabric3RequestContext context;

    /**
     * Creates a new instance.
     *
     * @param creditService  returns the applicant's credit score from a credit bureau
     * @param riskService    scores the loan risk
     * @param pricingService calculates loan options
     * @param loanChannel    the channel to publish loan events to
     * @param monitor        the monitor for recording errors
     */
    public RequestCoordinatorImpl(@Reference CreditService creditService,
                                  @Reference RiskAssessmentService riskService,
                                  @Reference PricingService pricingService,
                                  @Producer("loanChannel") LoanChannel loanChannel,
                                  @Monitor("LoanMonitorChannel") RequestMonitor monitor) {
        this.creditService = creditService;
        this.riskService = riskService;
        this.pricingService = pricingService;
        this.loanChannel = loanChannel;
        this.monitor = monitor;
    }

    @PersistenceContext(name = "loanApplicationEmf", unitName = "loanApplication")
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Context
    public void setContext(Fabric3RequestContext context) {
        this.context = context;
    }

    public long apply(LoanRequest request) throws LoanException {
        // create a loan application and process it
        LoanRecord record = createLoanRecord(request);

        // pull the applicant's credit score and update the loan record
        CreditScore score = creditService.score(record.getSsn());
        record.setCreditScore(score.getScore());
        em.persist(record);

        // synchronize to avoid race conditions with non-blocking risk assessment
        em.flush();
        monitor.received(record.getId());

        // publish an event that the loan application was received
        ApplicationReceived event = new ApplicationReceived();
        loanChannel.publish(event);

        // assess the risk
        RiskRequest riskRequest = new RiskRequest(record.getId(), record.getCreditScore(), record.getAmount(), record.getDownPayment());
        riskService.assessRisk(riskRequest);
        return record.getId();
    }

    public void cancel() {
        throw new UnsupportedOperationException();
    }

    @Consumer("loanChannel")
    public void onRiskAssessment(RiskAssessmentComplete event) {
        LoanRecord record;
        long id = event.getId();

        // record that the risk assessment was received
        monitor.riskAssessmentReceived(id);

        record = em.find(LoanRecord.class, id);
        if (record == null) {
            monitor.loanRecordNotFound(id);
            return;
        }

        if (RiskResponse.APPROVE == event.getDecision()) {
            // loan approved, price it
            record.setStatus(LoanService.PRICING);
            PricingRequest pricingRequest = new PricingRequest(id, event.getRiskFactor());
            pricingService.price(pricingRequest);
        } else {
            // loan declined
            record.setStatus(LoanService.REJECTED);
            loanChannel.publish(new ApplicationRejected());
        }
        em.merge(record);
    }

    public void onPrice(PriceResponse response) {
        long id = response.getId();
        LoanRecord record = em.find(LoanRecord.class, id);
        if (record == null) {
            monitor.loanRecordNotFound(id);
            return;
        }
        List<TermInfo> termInfos = new ArrayList<TermInfo>();
        for (PricingOption pricingOption : response.getOptions()) {
            TermInfo termInfo = new TermInfo();
            termInfo.setApr(pricingOption.getApr());
            termInfo.setRate(pricingOption.getRate());
            termInfo.setType(pricingOption.getType());
            termInfos.add(termInfo);
        }
        record.setTerms(termInfos);
        record.setStatus(LoanService.AWAITING_ACCEPTANCE);
        em.merge(record);
        loanChannel.publish(new ApplicationReady());
    }


    private LoanRecord createLoanRecord(LoanRequest request) {
        LoanRecord record = new LoanRecord();
        record.setSsn(request.getSSN());
        record.setEmail(request.getEmail());
        record.setAmount(request.getAmount());
        record.setDownPayment(request.getDownPayment());
        PropertyInfo info = new PropertyInfo(request.getPropertyAddress());
        record.setPropertyInfo(info);
        record.setStatus(LoanService.SUBMITTED);
        String username = context.getCurrentSubject().getUsername();
        record.setUsername(username);
        return record;
    }


}

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
import org.fabric3.samples.bigbank.api.channel.ApplicationEventChannel;
import org.fabric3.samples.bigbank.api.domain.LoanRecord;
import org.fabric3.samples.bigbank.api.domain.PropertyInfo;
import org.fabric3.samples.bigbank.api.domain.TermInfo;
import org.fabric3.samples.bigbank.api.event.ApplicationReady;
import org.fabric3.samples.bigbank.api.event.ApplicationReceived;
import org.fabric3.samples.bigbank.api.event.ManualRiskAssessment;
import org.fabric3.samples.bigbank.api.event.ManualRiskAssessmentComplete;
import org.fabric3.samples.bigbank.api.loan.LoanException;
import org.fabric3.samples.bigbank.api.loan.LoanService;
import org.fabric3.samples.bigbank.api.message.LoanRequest;
import org.fabric3.samples.bigbank.services.credit.CreditScore;
import org.fabric3.samples.bigbank.services.credit.CreditService;
import org.fabric3.samples.bigbank.services.pricing.PriceResponse;
import org.fabric3.samples.bigbank.services.pricing.PricingOption;
import org.fabric3.samples.bigbank.services.pricing.PricingRequest;
import org.fabric3.samples.bigbank.services.pricing.PricingService;
import org.fabric3.samples.bigbank.services.pricing.PricingServiceCallback;
import org.fabric3.samples.bigbank.services.risk.RiskAssessmentRequest;
import org.fabric3.samples.bigbank.services.risk.RiskAssessmentResponse;
import org.fabric3.samples.bigbank.services.risk.RiskAssessmentService;

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
    private ApplicationEventChannel loanChannel;
    private RequestMonitor monitor;
    private EntityManager em;
    private Fabric3RequestContext context;

    /**
     * Constructor.
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
                                  @Producer("loanChannel") ApplicationEventChannel loanChannel,
                                  @Monitor RequestMonitor monitor) {
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
        CreditScore creditScore = creditService.score(record.getSsn());
        int score = creditScore.getScore();
        record.setCreditScore(score);
        em.persist(record);

        long id = record.getId();
        monitor.received(id);

        // synchronize to avoid race conditions with non-blocking risk assessment
        em.flush();

        double amount = record.getAmount();

        // publish an event that the loan application was received
        ApplicationReceived event = new ApplicationReceived(id, amount);
        loanChannel.publish(event);
        
        // assess the risk
        double down = record.getDownPayment();
        RiskAssessmentRequest riskRequest = new RiskAssessmentRequest(id, score, amount, down);
        RiskAssessmentResponse response = riskService.assessRisk(riskRequest);

        if (RiskAssessmentResponse.APPROVED == response.getDecision()) {
            // approved immediately, price the loan
            record.setStatus(LoanService.PRICING);
            record.setApprovalType(LoanRecord.AUTOMATED_APPROVAL);
            PricingRequest pricingRequest = new PricingRequest(id, response.getRiskFactor());
            pricingService.price(pricingRequest);
            monitor.approved(id);
        } else if (RiskAssessmentResponse.REJECTED == response.getDecision()) {
            // loan not approved
            record.setStatus(LoanService.REJECTED);
            monitor.rejected(id);
        } else {
            // manual approval
            record.setStatus(LoanService.AWAITING_ASSESSMENT);
            record.setApprovalType(LoanRecord.MANUAL_APPROVAL);
            ManualRiskAssessment manualAssessment = new ManualRiskAssessment(id);
            loanChannel.publish(manualAssessment);
            monitor.manualAssessment(id);
        }
        return id;
    }

    public void cancel() {
        throw new UnsupportedOperationException();
    }

    @Consumer("loanChannel")
    public void onRiskAssessment(ManualRiskAssessmentComplete event) {
        if (event.isApproved()) {
            // loan approved, price it
            PricingRequest pricingRequest = new PricingRequest(event.getLoanId(), event.getRiskFactor());
            pricingService.price(pricingRequest);
        }  else {
            // TODO finish
        }
    }

    public void onPrice(PriceResponse response) {
        long id = response.getId();
        LoanRecord record = em.find(LoanRecord.class, id);
        if (record == null) {
            throw new AssertionError("Record was not found: " + id);
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
        double amount = record.getAmount();
        int approvalType = record.getApprovalType();
        ApplicationReady ready = new ApplicationReady(id, amount, approvalType);
        loanChannel.publish(ready);
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

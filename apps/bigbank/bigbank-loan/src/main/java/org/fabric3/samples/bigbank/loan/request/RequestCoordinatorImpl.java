/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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

import org.fabric3.api.annotation.monitor.Monitor;
import org.fabric3.samples.bigbank.api.loan.LoanApplicationNotFoundException;
import org.fabric3.samples.bigbank.api.loan.LoanException;
import org.fabric3.samples.bigbank.api.message.LoanRequest;
import org.fabric3.samples.bigbank.api.message.LoanStatus;
import org.fabric3.samples.bigbank.loan.domain.LoanRecord;
import org.fabric3.samples.bigbank.loan.domain.PropertyInfo;
import org.fabric3.samples.bigbank.loan.domain.TermInfo;
import org.fabric3.samples.bigbank.loan.monitor.ErrorMonitor;
import org.fabric3.samples.bigbank.loan.notification.NotificationService;
import org.fabric3.samples.bigbank.loan.store.StoreException;
import org.fabric3.samples.bigbank.loan.store.StoreService;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Service;

import java.util.ArrayList;
import java.util.List;

import org.fabric3.samples.bigbank.services.pricing.PricingServiceCallback;
import org.fabric3.samples.bigbank.services.pricing.PricingService;
import org.fabric3.samples.bigbank.services.pricing.PricingOption;
import org.fabric3.samples.bigbank.services.pricing.PriceResponse;
import org.fabric3.samples.bigbank.services.pricing.PricingRequest;
import org.fabric3.samples.bigbank.services.risk.RiskAssessmentCallback;
import org.fabric3.samples.bigbank.services.risk.RiskAssessmentService;
import org.fabric3.samples.bigbank.services.risk.RiskRequest;
import org.fabric3.samples.bigbank.services.risk.RiskResponse;
import org.fabric3.samples.bigbank.services.credit.CreditServiceCallback;
import org.fabric3.samples.bigbank.services.credit.CreditService;
import org.fabric3.samples.bigbank.services.credit.CreditScore;

/**
 * Default implementation of the RequestCoordinator service.
 *
 * @version $Revision: 8764 $ $Date: 2010-03-29 12:00:55 +0200 (Mon, 29 Mar 2010) $
 */
@Service(names = {RequestCoordinator.class,
        CreditServiceCallback.class,
        RiskAssessmentCallback.class,
        PricingServiceCallback.class})
public class RequestCoordinatorImpl
        implements RequestCoordinator, CreditServiceCallback, RiskAssessmentCallback, PricingServiceCallback {
    // simple counter
    private CreditService creditService;
    private RiskAssessmentService riskService;
    private PricingService pricingService;
    private NotificationService notificationService;
    private StoreService storeService;
    private ErrorMonitor monitor;

    /**
     * Creates a new instance.
     *
     * @param creditService       returns the applicant's credit score from a credit bureau
     * @param riskService         scores the loan risk
     * @param pricingService      calculates loan options
     * @param notificationService notifies the loan applicant of loan events
     * @param storeService        stores an application after it has been processed
     * @param monitor             the monitor for recording errors
     */
    public RequestCoordinatorImpl(@Reference(name = "creditService") CreditService creditService,
                                  @Reference(name = "riskService") RiskAssessmentService riskService,
                                  @Reference(name = "pricingService") PricingService pricingService,
                                  @Reference(name = "notificationService") NotificationService notificationService,
                                  @Reference(name = "storeService") StoreService storeService,
                                  @Monitor ErrorMonitor monitor) {
        this.creditService = creditService;
        this.riskService = riskService;
        this.pricingService = pricingService;
        this.notificationService = notificationService;
        this.storeService = storeService;
        this.monitor = monitor;
    }

    public long start(LoanRequest request) throws LoanException {
        // create a loan application and process it
        LoanRecord record = new LoanRecord();
        record.setSsn(request.getSSN());
        record.setEmail(request.getEmail());
        record.setAmount(request.getAmount());
        record.setDownPayment(request.getDownPayment());
        PropertyInfo info = new PropertyInfo(request.getPropertyAddress());
        record.setPropertyInfo(info);
        record.setStatus(LoanStatus.SUBMITTED);
        try {
            storeService.save(record);
        } catch (StoreException e) {
            throw new LoanException(e);
        }
        // pull the applicant's credit score
        creditService.score(record.getSsn());
        return record.getId();
    }

    public void cancel() {
        throw new UnsupportedOperationException();
    }

    public void onCreditScore(CreditScore result) {
        // assess the loan risk
        System.out.println("CreditServiceCallback: Received credit score");

        String ssn = result.getSsn();
        LoanRecord record;
        try {
            record = storeService.findBySSN(ssn);
        } catch (StoreException e) {
            monitor.onError(e);
            return;
        }
        if (record == null) {
            monitor.onErrorMessage("No record found for SSN: " + ssn);
            return;
        }
        record.setCreditScore(result.getScore());
        try {
            storeService.update(record);
        } catch (StoreException e) {
            monitor.onError(e);
        }
        RiskRequest request = new RiskRequest(record.getId(), record.getCreditScore(), record.getAmount(), record.getDownPayment());
        riskService.assessRisk(request);
    }

    public void creditScoreError(Exception exception) {
        monitor.onError(exception);
    }

    public void onAssessment(RiskResponse response) {
        System.out.println("RiskAssessmentCallback: received risk assessment");
        LoanRecord record;
        long id = response.getId();
        try {
            record = findRecord(id);
        } catch (LoanException e) {
            monitor.onError(e);
            return;
        }
        PricingRequest pricingRequest = new PricingRequest(id, response.getRiskFactor());
        if (RiskResponse.APPROVE == response.getDecision()) {
            // calculate the terms
            pricingService.price(pricingRequest);
        } else {
            // declined
            try {
                record.setStatus(LoanStatus.REJECTED);
                storeService.save(record);
                // notify the client
                notificationService.rejected(record.getEmail(), record.getId());
            } catch (StoreException e) {
                monitor.onError(e);
            }

        }
    }

    public void onPrice(PriceResponse response) {
        System.out.println("PricingServiceCallback: received pricing response");
        LoanRecord record;
        try {
            record = findRecord(response.getId());
        } catch (LoanException e) {
            monitor.onError(e);
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
        try {
            record.setStatus(LoanStatus.AWAITING_ACCEPTANCE);
            storeService.update(record);
            // notify the client
            notificationService.approved(record.getEmail(), record.getId());
        } catch (StoreException e) {
            monitor.onError(e);
        }

    }

    public void riskAssessmentError(Exception exception) {
        monitor.onError(exception);
    }


    private LoanRecord findRecord(long id) throws LoanException {
        LoanRecord record;
        try {
            record = storeService.find(id);
        } catch (StoreException e) {
            throw new LoanException(e);
        }
        if (record == null) {
            throw new LoanApplicationNotFoundException("No loan application on file with id " + id);
        }
        return record;
    }


}

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
package org.fabric3.samples.bigbank.loan.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.oasisopen.sca.annotation.ManagedTransaction;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Service;

import org.fabric3.api.annotation.Producer;
import org.fabric3.api.annotation.monitor.Monitor;
import org.fabric3.samples.bigbank.api.event.ApplicationReceived;
import org.fabric3.samples.bigbank.api.event.LoanChannel;
import org.fabric3.samples.bigbank.api.loan.LoanApplication;
import org.fabric3.samples.bigbank.api.loan.LoanApplicationStatus;
import org.fabric3.samples.bigbank.api.loan.LoanService;
import org.fabric3.samples.bigbank.domain.LoanRecord;
import org.fabric3.samples.bigbank.domain.LoanStatus;
import org.fabric3.samples.bigbank.domain.RiskDecision;
import org.fabric3.samples.bigbank.domain.RiskInfo;
import org.fabric3.samples.bigbank.domain.RiskReasonInfo;
import org.fabric3.samples.bigbank.loan.gateway.LoanGateway;
import org.fabric3.samples.bigbank.loan.gateway.ResponseQueue;
import org.fabric3.samples.bigbank.loan.recovery.LoanRecovery;
import org.fabric3.samples.bigbank.loan.rest.RsLoanService;
import org.fabric3.samples.bigbank.rate.Rating;
import org.fabric3.samples.bigbank.rate.RatingRequest;
import org.fabric3.samples.bigbank.rate.RatingService;
import org.fabric3.samples.bigbank.rate.RatingServiceCallback;
import org.fabric3.samples.bigbank.risk.RiskAssessmentRequest;
import org.fabric3.samples.bigbank.risk.RiskAssessmentResponse;
import org.fabric3.samples.bigbank.risk.RiskReason;
import org.fabric3.samples.bigbank.risk.RiskService;
import org.fabric3.samples.bigbank.util.GenericsHelper;

/**
 * Receives loan applications and manages the approval process. This service delegates to a {@link RatingService} in order to tier the application
 * based on scores obtained from third-part credit bureaus. When a rating is received, this service delegates to a {@link RiskService} to either
 * approve or reject the application.
 * <p/>
 * This service is exposed over multiple transports, including REST/HTTP, WS-* and a file system batch import.
 * <p/>
 * This implementation demonstrates the use of distributed asynchronous (non-blocking) service invocations, which are useful for applications
 * requiring high-throughput and where responses may take an extended period of time to generate. Specifically, the {@link RatingService} is called
 * asynchronously over JMS or ZeroMQ when deployed to a distributed environment (in a single VM environment the call will be conducted in-process on a
 * separate thread). A response is received via the {@link #onResults(Rating)} callback.
 * <p/>
 * Note this implementation also demonstrates the use of JPA and global managed (JTA) transactions.
 *
 * @version $Revision$ $Date$
 */
@ManagedTransaction
@Service(names = {LoanService.class, LoanRecovery.class, LoanGateway.class, RsLoanService.class, RatingServiceCallback.class})
public class LoanServiceImpl implements LoanService, LoanGateway, RsLoanService, LoanRecovery, RatingServiceCallback {
    private RatingService ratingService;
    private RiskService riskService;
    private LoanChannel channel;
    private ResponseQueue responseQueue;
    private JAXBContext context;

    private LoanMonitor monitor;

    @PersistenceContext(unitName = "loanApplication")
    protected EntityManager em;

    public LoanServiceImpl(@Reference RatingService ratingService,
                           @Reference RiskService riskService,
                           @Reference(name = "responseQueue") ResponseQueue responseQueue,
                           @Producer("loanChannel") LoanChannel channel,
                           @Monitor LoanMonitor monitor) throws JAXBException {
        this.ratingService = ratingService;
        this.riskService = riskService;
        this.responseQueue = responseQueue;
        this.channel = channel;
        this.monitor = monitor;
        context = JAXBContext.newInstance("org.fabric3.samples.bigbank.api.loan");
    }

    public void apply(LoanApplication application) {
        // create a loan application and persist it - note a JTA transaction is now active and will commit when the results are returned
        LoanRecord record = createLoanRecord(application);
        em.persist(record);

        monitor.received(record.getEin());

        ApplicationReceived event = new ApplicationReceived(record.getId(), record.getAmount());
        channel.publish(event);

        // synchronize to avoid race conditions with non-blocking rating
        em.flush();

        // rate the application
        rate(record);
    }

    public LoanApplicationStatus getStatus(String correlation) {
        Query query = em.createQuery("SELECT l FROM LoanRecord l WHERE l.clientCorrelation = :number");
        query.setParameter("number", correlation);
        try {
            LoanRecord record = GenericsHelper.cast(query.getSingleResult());
            return new LoanApplicationStatus(correlation, record.getStatus().toString());
        } catch (NoResultException e) {
            return new LoanApplicationStatus(correlation, LoanApplicationStatus.INVALID);
        }
    }

    public LoanApplicationStatus getRSStatus(String correlation) {
        LoanApplicationStatus status = getStatus(correlation);
        if (LoanApplicationStatus.INVALID.equals(status.getStatus())) {
            throw new WebApplicationException(404);
        }
        return status;

    }

    public void process(InputStream stream) throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        JAXBElement<LoanApplication> element = GenericsHelper.cast(unmarshaller.unmarshal(stream));
        apply(element.getValue());
    }

    public void recover(LoanRecord record) {
        rate(record);
    }

    public void onResults(Rating rating) {
        // received a rate, now assess the risk
        LoanRecord record = em.find(LoanRecord.class, rating.getCorrelationId());
        record.setStatus(LoanStatus.RATED);
        assesRisk(record, rating);
        completed(record);
    }

    private LoanRecord createLoanRecord(LoanApplication application) {
        LoanRecord record = new LoanRecord();
        record.setAmount(application.getAmount());
        record.setStatus(LoanStatus.SUBMITTED);
        record.setEin(application.getEin());
        record.setClientCorrelation(application.getClientCorrelation());
        record.setNotificationAddress(application.getNotificationAddress());
        return record;
    }

    private void rate(LoanRecord record) {
        // rate the applicant - results will be received by {@link #onResults(Rating)}
        String ein = record.getEin();
        long id = record.getId();
        RatingRequest ratingRequest = new RatingRequest(ein, id);
        ratingService.rate(ratingRequest);
    }

    private void assesRisk(LoanRecord record, Rating rating) {
        RiskAssessmentRequest request = new RiskAssessmentRequest(rating.getEin(), rating.getTier(), record.getAmount());
        RiskAssessmentResponse response = riskService.assessRisk(request);
        RiskAssessmentResponse.Decision decision = response.getDecision();
        if (RiskAssessmentResponse.Decision.APPROVED == decision) {
            monitor.approved(record.getEin());
        } else {
            monitor.rejected(record.getEin());
        }

        List<RiskReasonInfo> reasons = new ArrayList<RiskReasonInfo>();
        for (RiskReason riskReason : response.getReasons()) {
            reasons.add(new RiskReasonInfo(riskReason.getCode(), riskReason.getDescription()));
        }
        RiskDecision riskDecision = decision == RiskAssessmentResponse.Decision.APPROVED ? RiskDecision.APPROVED : RiskDecision.REJECTED;
        RiskInfo info = new RiskInfo(riskDecision, reasons);
        record.setRiskInfo(info);
        record.setStatus(LoanStatus.FINAL);
    }

    private void completed(LoanRecord record) {
        if ("file".equals(record.getNotificationAddress())) {
            try {
                Marshaller marshaller = context.createMarshaller();
                LoanApplicationStatus status = new LoanApplicationStatus(record.getClientCorrelation(), record.getStatus().toString());
                OutputStream stream = responseQueue.openStream(record.getClientCorrelation() + ".xml");
                marshaller.marshal(status, stream);
                stream.close();
            } catch (JAXBException e) {
                monitor.error("Error sending notification: " + record.getId(), e);
            } catch (IOException e) {
                monitor.error("Error closing stream: " + record.getId(), e);
            }

        }

    }


}

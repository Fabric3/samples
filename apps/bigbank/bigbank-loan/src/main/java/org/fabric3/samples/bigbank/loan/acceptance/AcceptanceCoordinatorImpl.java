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
package org.fabric3.samples.bigbank.loan.acceptance;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.oasisopen.sca.annotation.Context;
import org.oasisopen.sca.annotation.ManagedTransaction;
import org.oasisopen.sca.annotation.Reference;

import org.fabric3.api.Fabric3RequestContext;
import org.fabric3.api.annotation.Consumer;
import org.fabric3.api.annotation.monitor.Monitor;
import org.fabric3.samples.bigbank.api.domain.LoanRecord;
import org.fabric3.samples.bigbank.api.domain.TermInfo;
import org.fabric3.samples.bigbank.api.event.AppraisalEvent;
import org.fabric3.samples.bigbank.api.event.AppraisalResult;
import org.fabric3.samples.bigbank.api.event.AppraisalScheduled;
import org.fabric3.samples.bigbank.api.loan.LoanApplicationNotFoundException;
import org.fabric3.samples.bigbank.api.loan.LoanException;
import org.fabric3.samples.bigbank.api.loan.LoanService;
import org.fabric3.samples.bigbank.api.message.LoanApplication;
import org.fabric3.samples.bigbank.api.message.LoanOption;
import org.fabric3.samples.bigbank.api.message.LoanStatus;
import org.fabric3.samples.bigbank.services.appraisal.AppraisalRequest;
import org.fabric3.samples.bigbank.services.appraisal.AppraisalService;

import static org.fabric3.samples.bigbank.util.GenericsHelper.cast;

/**
 * Default implementation of the AcceptanceCoordinator.
 *
 * @version $Revision$ $Date$
 */
@ManagedTransaction
public class AcceptanceCoordinatorImpl implements AcceptanceCoordinator {
    private AppraisalService appraisalService;
    private AcceptanceMonitor monitor;
    private EntityManager em;
    private Fabric3RequestContext context;

    public AcceptanceCoordinatorImpl(@Reference(name = "appraisalService") AppraisalService appraisalService, @Monitor AcceptanceMonitor monitor) {
        this.appraisalService = appraisalService;
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

    public LoanApplication retrieve(long loanId) throws LoanException {
        LoanRecord record = em.find(LoanRecord.class, loanId);
        if (record == null) {
            throw new LoanApplicationNotFoundException("Loan record not found");
        }
        return createApplication(record);
    }

    public List<LoanApplication> retrieveApplications(int status) {
        String username = context.getCurrentSubject().getUsername();
        Query query = em.createQuery("SELECT l FROM LoanRecord l WHERE l.username = :username AND l.status = :status");
        query.setParameter("username", username);
        query.setParameter("status", status);
        List<LoanRecord> records = cast(query.getResultList());
        List<LoanApplication> list = new ArrayList<LoanApplication>(records.size());
        for (LoanRecord record : records) {
            LoanApplication application = createApplication(record);
            list.add(application);
        }
        return list;
    }

    public List<LoanStatus> getLoanStatus() {
        String username = context.getCurrentSubject().getUsername();
        Query query = em.createQuery("SELECT l FROM LoanRecord l WHERE l.username = :username");
        query.setParameter("username", username);
        List<LoanRecord> records = cast(query.getResultList());
        List<LoanStatus> list = new ArrayList<LoanStatus>(records.size());
        for (LoanRecord record : records) {
            double total = record.getAmount() - record.getDownPayment();
            list.add(new LoanStatus(record.getId(), total, record.getStatus()));
        }
        return list;
    }

    public void accept(long id, String type) throws LoanException {
        LoanRecord record = em.find(LoanRecord.class, id);
        if (record == null) {
            throw new LoanApplicationNotFoundException("Loan record not found");
        }
        List<TermInfo> terms = record.getTerms();
        boolean found = false;
        for (TermInfo term : terms) {
            if (term.getType().equals(type)) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new InvalidLoanOptionException("Invalid loan option selected for loan " + id);
        }
        record.setTypeSelected(type);
        record.setStatus(LoanService.AWAITING_APPRAISAL);
        em.merge(record);
        AppraisalRequest request = new AppraisalRequest(id, record.getPropertyInfo().getAddress());
        appraisalService.appraise(request);
        monitor.accepted(id);
    }

    public void decline(long id) throws LoanException {
        LoanRecord record = em.find(LoanRecord.class, id);
        if (record == null) {
            throw new LoanApplicationNotFoundException("Loan record not found");
        }
        record.setStatus(LoanService.DECLINED);
        em.merge(record);
        monitor.declined(id);
    }

    @Consumer("loanChannel")
    public void onEvent(AppraisalEvent event) {
        if (event instanceof AppraisalScheduled) {
            appraisalScheduled((AppraisalScheduled) event);
        } else if (event instanceof AppraisalResult) {
            appraisalCompleted((AppraisalResult) event);
        }
    }

    private void appraisalScheduled(AppraisalScheduled scheduled) {
        try {
            long id = scheduled.getLoanId();
            LoanRecord record = em.find(LoanRecord.class, id);
            if (record == null) {
                throw new LoanApplicationNotFoundException("Loan record not found");
            }
            monitor.appraisalScheduled(id);
        } catch (LoanException e) {
            monitor.onError(e);
        }
    }

    private void appraisalCompleted(AppraisalResult result) {
        long id = result.getLoanId();
        if (AppraisalResult.DECLINED == result.getResult()) {
            monitor.appraisalDeclined(id);
        } else {
            monitor.appraisalCompleted(id);
        }
    }

    private LoanApplication createApplication(LoanRecord record) {
        LoanApplication application = new LoanApplication();
        application.setId(record.getId());
        application.setAmount(record.getAmount());
        LoanOption[] options = new LoanOption[record.getTerms().size()];
        for (int i = 0; i < record.getTerms().size(); i++) {
            TermInfo term = record.getTerms().get(i);
            LoanOption loanOption = new LoanOption(term.getType(), term.getRate(), term.getApr());
            options[i] = loanOption;
        }
        application.setOptions(options);
        return application;
    }


}

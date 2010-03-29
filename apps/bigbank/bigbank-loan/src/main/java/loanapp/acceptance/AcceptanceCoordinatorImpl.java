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
package loanapp.acceptance;

import org.fabric3.samples.bigbank.api.loan.LoanApplicationNotFoundException;
import org.fabric3.samples.bigbank.api.loan.LoanException;
import org.fabric3.samples.bigbank.api.message.LoanApplication;
import org.fabric3.samples.bigbank.api.message.LoanOption;
import org.fabric3.samples.bigbank.api.message.LoanStatus;
import loanapp.appraisal.AppraisalCallback;
import loanapp.appraisal.AppraisalRequest;
import loanapp.appraisal.AppraisalResult;
import loanapp.appraisal.AppraisalSchedule;
import loanapp.appraisal.AppraisalService;
import loanapp.domain.LoanRecord;
import loanapp.domain.TermInfo;
import loanapp.monitor.ErrorMonitor;
import loanapp.notification.NotificationService;
import loanapp.store.StoreException;
import loanapp.store.StoreService;
import org.fabric3.api.annotation.Monitor;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Default implementation of the AcceptanceCoordinator.
 *
 * @version $Revision$ $Date$
 */
@Service(names = {AcceptanceCoordinator.class, AppraisalCallback.class})
public class AcceptanceCoordinatorImpl implements AcceptanceCoordinator, AppraisalCallback {
    private AppraisalService appraisalService;
    private NotificationService notificationService;
    private StoreService storeService;
    private ErrorMonitor monitor;

    public AcceptanceCoordinatorImpl(@Reference(name = "appraisalService") AppraisalService appraisalService,
                                     @Reference(name = "notificationService") NotificationService notificationService,
                                     @Reference(name = "storeService") StoreService storeService,
                                     @Monitor ErrorMonitor monitor) {
        this.appraisalService = appraisalService;
        this.notificationService = notificationService;
        this.storeService = storeService;
        this.monitor = monitor;
    }


    public LoanApplication retrieve(long loanId) throws LoanException {
        LoanRecord record = findRecord(loanId);
        LoanApplication application = new LoanApplication();
        LoanOption[] options = new LoanOption[record.getTerms().size()];
        for (int i = 0; i < record.getTerms().size(); i++) {
            TermInfo term = record.getTerms().get(i);
            LoanOption loanOption = new LoanOption(term.getType(), term.getRate(), term.getApr());
            options[i] = loanOption;
        }
        application.setOptions(options);
        return application;
    }

    public void accept(long id, String type) throws LoanException {
        LoanRecord record = findRecord(id);
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
        record.setStatus(LoanStatus.AWAITING_APPRAISAL);
        try {
            storeService.update(record);
        } catch (StoreException e) {
            throw new LoanException(e);
        }
        AppraisalRequest request = new AppraisalRequest(id, record.getPropertyInfo().getAddress());
        appraisalService.appraise(request);
    }

    public void decline(long id) throws LoanException {
        LoanRecord record = findRecord(id);
        record.setStatus(LoanStatus.DECLINED);
        try {
            storeService.update(record);
        } catch (StoreException e) {
            throw new LoanException(e);
        }
    }

    public void schedule(AppraisalSchedule schedule) {
        try {
            long id = schedule.getId();
            LoanRecord record = findRecord(id);
            String email = record.getEmail();
            Date date = schedule.getDate();
            notificationService.appraisalScheduled(email, id, date);
        } catch (LoanException e) {
            monitor.onError(e);
        }
    }

    public void appraisalCompleted(AppraisalResult result) {
        if (AppraisalResult.DECLINED == result.getResult()) {
            // just return
            return;
        }
        try {
            LoanRecord record = findRecord(result.getId());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, 1);
            notificationService.fundingDateScheduled(record.getEmail(), record.getId(), calendar.getTime());
        } catch (LoanException e) {
            monitor.onError(e);
        }
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

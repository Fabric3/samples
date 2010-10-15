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
package org.fabric3.samples.bigbank.loan.statistics;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.ManagedTransaction;
import org.oasisopen.sca.annotation.Scope;

import org.fabric3.api.annotation.Consumer;
import org.fabric3.api.annotation.Producer;
import org.fabric3.api.annotation.Resource;
import org.fabric3.api.annotation.management.Management;
import org.fabric3.api.annotation.management.ManagementOperation;
import org.fabric3.samples.bigbank.api.domain.ApplicationStatistics;
import org.fabric3.samples.bigbank.api.domain.LoanRecord;
import org.fabric3.samples.bigbank.api.event.ApplicationEvent;
import org.fabric3.samples.bigbank.api.event.ApplicationExpired;
import org.fabric3.samples.bigbank.api.event.ApplicationReady;
import org.fabric3.samples.bigbank.api.event.ApplicationReceived;
import org.fabric3.samples.bigbank.api.event.AppraisalScheduled;
import org.fabric3.samples.bigbank.api.event.ManualRiskAssessmentComplete;
import org.fabric3.samples.bigbank.api.event.RunningAveragesUpdateEvent;

import static org.fabric3.samples.bigbank.util.GenericsHelper.cast;

/**
 * @version $Rev$ $Date$
 */
@EagerInit
@Management
@ManagedTransaction
@Scope("COMPOSITE")
public class ApplicationStatisticsComponent {
    private ScheduledExecutorService executorService;
    private StatisticsChannel statisticsChannel;
    private EntityManager em;

    private StatisticsRunnable runnable;
    private volatile long wait = 10000;

    private MovingAverage amountAverage;
    private MovingAverage approvalAverage;
    private MovingAverage timeToCompleteAutomatedAverage;
    private MovingAverage timeToCompleteManualAverage;

    @PersistenceContext(unitName = "loanApplication")
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Producer
    public void setStatisticsChannel(StatisticsChannel channel) {
        this.statisticsChannel = channel;
    }

    @Resource
    public void setExecutorService(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    @ManagementOperation(description = "Sets the period to wait between moving average updates")
    public void setWait(long wait) {
        this.wait = wait;
    }

    @Init
    public void init() throws IOException {
        amountAverage = new MovingAverage(4);
        approvalAverage = new MovingAverage(4);
        timeToCompleteAutomatedAverage = new MovingAverage(4);
        timeToCompleteManualAverage = new MovingAverage(4);

        runnable = new StatisticsRunnable();
        executorService.schedule(runnable, wait, TimeUnit.MILLISECONDS);
    }

    @Destroy
    public void destroy() throws IOException {
        if (runnable != null) {
            runnable.stop();
        }
    }

    @Consumer("loanChannel")
    public void onEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReceived) {
            onReceived((ApplicationReceived) event);
        } else if (event instanceof ManualRiskAssessmentComplete) {
            onAssessmentComplete((ManualRiskAssessmentComplete) event);
        } else if (event instanceof ApplicationReady) {
            onApplicationReady((ApplicationReady) event);
        } else if (event instanceof AppraisalScheduled) {
            onAppraisalScheduled((AppraisalScheduled) event);
        } else if (event instanceof ApplicationExpired) {
            onExpired((ApplicationExpired) event);
        }
    }

    private void onAppraisalScheduled(AppraisalScheduled event) {
    }

    private void onReceived(ApplicationReceived event) {
        LoanRecord record = getRecord(event);
        double amount = record.getAmount();
        amountAverage.write(amount);
    }

    private void onAssessmentComplete(ManualRiskAssessmentComplete event) {
    }

    private void onApplicationReady(ApplicationReady event) {
        LoanRecord record = getRecord(event);
        double amount = record.getAmount();
        approvalAverage.write(amount);

        ApplicationStatistics statistics = findStatistics(event.getLoanId());
        long elapsed = event.getTimestamp() - statistics.getReceivedTimestamp();
        if (LoanRecord.AUTOMATED_APPROVAL == record.getApprovalType()){
            timeToCompleteAutomatedAverage.write(elapsed);
        } else {
            // manual approval
            timeToCompleteManualAverage.write(elapsed);
        }

    }

    private void onExpired(ApplicationExpired event) {
    }

    private LoanRecord getRecord(ApplicationEvent event) {
        long id = event.getLoanId();
        LoanRecord record = em.find(LoanRecord.class, id);
        if (record == null) {
            throw new AssertionError("Loan record not found: " + id);
        }
        return record;
    }

    private ApplicationStatistics findStatistics(long loanId) {
        Query query = em.createQuery("SELECT l FROM ApplicationStatistics l WHERE l.loanId = :loanId");
        query.setParameter("loanId", loanId);
        ApplicationStatistics statistics = cast(query.getSingleResult());
        if (statistics == null) {
            throw new AssertionError("Statistics was null: " + loanId);
        }
        return statistics;
    }

    /**
     * Reads moving average and periodically publishes updates to the statistics channel.
     */
    private class StatisticsRunnable implements Runnable {
        private boolean stop;

        public void stop() {
            this.stop = true;
        }

        public void run() {
            RunningAveragesUpdateEvent event = new RunningAveragesUpdateEvent();
            double amount = amountAverage.readAverage();
            event.setRequestAmount(amount);
            amount = approvalAverage.readAverage();
            event.setApprovalAmount(amount);
            event.setTimeToAutomatedApproval((long) timeToCompleteAutomatedAverage.readAverage());
            event.setTimeToManualApproval((long) timeToCompleteManualAverage.readAverage());

            statisticsChannel.update(event);
            if (!stop) {
                // schedule the next read
                executorService.schedule(this, wait, TimeUnit.MILLISECONDS);
            }
        }
    }

}

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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.ManagedTransaction;
import org.oasisopen.sca.annotation.Scope;

import org.fabric3.api.annotation.Consumer;
import org.fabric3.api.annotation.monitor.Monitor;
import org.fabric3.samples.bigbank.api.event.ApplicationEvent;
import org.fabric3.samples.bigbank.api.event.ApplicationExpired;
import org.fabric3.samples.bigbank.api.event.ApplicationReady;
import org.fabric3.samples.bigbank.api.event.ApplicationReceived;
import org.fabric3.samples.bigbank.api.event.AppraisalScheduled;
import org.fabric3.samples.bigbank.api.event.ManualRiskAssessmentComplete;
import org.fabric3.samples.bigbank.api.domain.ApplicationStatistics;

/**
 * @version $Rev: 9526 $ $Date: 2010-10-10 15:32:06 +0200 (Sun, 10 Oct 2010) $
 */
@Scope("COMPOSITE")
@EagerInit
@ManagedTransaction
public class ApplicationEventRecorder {
    private EntityManager em;
    private RecorderMonitor monitor;

    public ApplicationEventRecorder(@Monitor RecorderMonitor monitor) {
        this.monitor = monitor;
    }

    @PersistenceContext(unitName = "loanApplication")
    public void setEntityManager(EntityManager em) {
        this.em = em;
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
        long id = event.getLoanId();
        ApplicationStatistics statistics = em.find(ApplicationStatistics.class, id);
        if (statistics == null) {
            monitor.statisticsNotFound(id);
            return;
        }
        statistics.setAppraisalScheduledTimestamp(System.currentTimeMillis());
        em.persist(statistics);
    }

    private void onReceived(ApplicationReceived event) {
        ApplicationStatistics statistics = new ApplicationStatistics();
        statistics.setLoanNumber(event.getLoanId());
        statistics.setReceivedTimestamp(System.currentTimeMillis());
        em.persist(statistics);
    }

    private void onAssessmentComplete(ManualRiskAssessmentComplete event) {
        long id = event.getLoanId();
        ApplicationStatistics statistics = em.find(ApplicationStatistics.class, id);
        if (statistics == null) {
            monitor.statisticsNotFound(id);
            return;
        }
        if (event.isApproved()) {
            statistics.setApprovedTimestamp(System.currentTimeMillis());
        } else {
            statistics.setRejectedTimestamp(System.currentTimeMillis());
        }
        em.persist(statistics);
    }

    private void onApplicationReady(ApplicationReady event) {
        long id = event.getLoanId();
        ApplicationStatistics statistics = em.find(ApplicationStatistics.class, id);
        if (statistics == null) {
            monitor.statisticsNotFound(id);
            return;
        }
        statistics.setReadyTimestamp(System.currentTimeMillis());
        em.persist(statistics);
    }

    private void onExpired(ApplicationExpired event) {
        long id = event.getLoanId();
        ApplicationStatistics statistics = em.find(ApplicationStatistics.class, id);
        if (statistics == null) {
            monitor.statisticsNotFound(id);
            return;
        }
        statistics.setExpiredTimestamp(System.currentTimeMillis());
        em.persist(statistics);
    }

}

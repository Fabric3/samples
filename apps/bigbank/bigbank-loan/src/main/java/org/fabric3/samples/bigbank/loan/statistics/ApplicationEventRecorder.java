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

import org.oasisopen.sca.annotation.ManagedTransaction;
import org.oasisopen.sca.annotation.Scope;

import org.fabric3.api.annotation.Consumer;
import org.fabric3.api.annotation.monitor.Monitor;
import org.fabric3.samples.bigbank.api.event.ApplicationEvent;
import org.fabric3.samples.bigbank.api.event.ApplicationReceived;
import org.fabric3.samples.bigbank.api.event.RiskAssessmentComplete;
import org.fabric3.samples.bigbank.loan.domain.ApplicationStatistics;

/**
 * @version $Rev: 9526 $ $Date: 2010-10-10 15:32:06 +0200 (Sun, 10 Oct 2010) $
 */
@Scope("COMPOSITE")
@ManagedTransaction
public abstract class ApplicationEventRecorder {
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
        long loanId = event.getLoanId();
        if (event instanceof ApplicationReceived) {
            ApplicationStatistics statistics = new ApplicationStatistics();
            statistics.setLoanNumber(loanId);
            statistics.setReceivedTimestamp(System.currentTimeMillis());
            em.persist(statistics);
        } else if (event instanceof RiskAssessmentComplete) {
            ApplicationStatistics statistics = em.find(ApplicationStatistics.class, event.getLoanId());
            if (statistics == null) {
                monitor.statisticsNotFound(loanId);
                return;
            }
        }
    }

}

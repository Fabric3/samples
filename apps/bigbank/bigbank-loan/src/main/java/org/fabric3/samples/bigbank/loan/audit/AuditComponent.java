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
package org.fabric3.samples.bigbank.loan.audit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.ManagedTransaction;
import org.oasisopen.sca.annotation.Scope;

import org.fabric3.api.annotation.Consumer;
import org.fabric3.samples.bigbank.api.event.ApplicationApproved;
import org.fabric3.samples.bigbank.api.event.ApplicationEvent;
import org.fabric3.samples.bigbank.api.event.ApplicationReceived;
import org.fabric3.samples.bigbank.api.event.ApplicationRejected;
import org.fabric3.samples.bigbank.domain.ApplicationStatistics;
import org.fabric3.samples.bigbank.util.GenericsHelper;

/**
 * Receives loan application events from the loan channel and records them for audit purposes.
 * <p/>
 * This implementation demonstrates how to create transactional event consumers.
 *
 * @version $Rev$ $Date$
 */
@EagerInit
@ManagedTransaction
@Scope("COMPOSITE")
public class AuditComponent {

    @PersistenceContext(unitName = "loanApplication")
    protected EntityManager em;

    @Consumer("loanChannel")
    public void onEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReceived) {
            onReceived((ApplicationReceived) event);
        } else if (event instanceof ApplicationRejected) {
            onReceived((ApplicationRejected) event);
        } else if (event instanceof ApplicationApproved) {
            onReceived((ApplicationApproved) event);
        }
    }

    private void onReceived(ApplicationRejected event) {
        ApplicationStatistics statistics = findStatistics(event.getLoanId());
        if (statistics == null) {
            return;
        }
        statistics.setRejectedTimestamp(event.getTimestamp());
    }

    private void onReceived(ApplicationApproved event) {
        ApplicationStatistics statistics = findStatistics(event.getLoanId());
        if (statistics == null) {
            return;
        }
        statistics.setApprovedTimestamp(event.getTimestamp());
    }

    private void onReceived(ApplicationReceived event) {
        ApplicationStatistics statistics = new ApplicationStatistics();
        statistics.setLoanId(event.getLoanId());
        statistics.setReceivedTimestamp(System.currentTimeMillis());
        em.persist(statistics);
    }


    private ApplicationStatistics findStatistics(long loanId) {
        Query query = em.createQuery("SELECT l FROM ApplicationStatistics l WHERE l.loanId = :loanId");
        query.setParameter("loanId", loanId);
        return GenericsHelper.cast(query.getSingleResult());
    }
}

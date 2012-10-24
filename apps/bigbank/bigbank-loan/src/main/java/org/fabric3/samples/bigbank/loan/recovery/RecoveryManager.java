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
package org.fabric3.samples.bigbank.loan.recovery;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.oasisopen.sca.annotation.ManagedTransaction;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;

import org.fabric3.samples.bigbank.domain.LoanRecord;
import org.fabric3.samples.bigbank.domain.LoanStatus;
import org.fabric3.samples.bigbank.util.GenericsHelper;

/**
 * A timer component that runs periodically checking for failed requests. A failed request can occur if a loan application is submitted and a request
 * to an external credit service fails periodically resulting in the process unable to complete.
 * <p/>
 * This implementation demonstrates how timer components can be use to provide guaranteed processing without requiring complex retry logic be placed
 * in all potential failure points of an application. For example, the rating service does not need to implement retry logic or rely on reliable
 * messaging and re-delivery. Instead, it can simply fail and this timer will re-initiate the request at a later point.
 * <p/>
 * This implementation relies on highly-available clustered singletons since only one timer should be active at a time. This is achieved through use
 * of the Fabric3-specific DOMAIN scope. If one runtime fails, Fabric3 will transparently activate another instance on a different runtime in the same
 * zone.
 * <p/>
 * Finally, this implementation also demonstrates the use of transactional timer components. The {@link ManagedTransaction} annotation instructs
 * Fabric3 to execute {@link #run()} in the context of a global (JTA) managed transaction.
 *
 * @version $Rev$ $Date$
 */
@Scope("DOMAIN")
@ManagedTransaction
public class RecoveryManager implements Runnable {
    @Reference
    protected LoanRecovery coordinator;

    @PersistenceContext(unitName = "loanApplication")
    protected EntityManager em;

    public void run() {
        Query query = em.createQuery("SELECT l FROM LoanRecord l WHERE l.status = :status and l.timestamp < :timestamp");
        query.setParameter("status", LoanStatus.SUBMITTED);
        query.setParameter("timestamp", System.currentTimeMillis() - 60000);

        List<LoanRecord> records = GenericsHelper.cast(query.getResultList());
        for (LoanRecord record : records) {
            coordinator.recover(record);
        }
    }


}

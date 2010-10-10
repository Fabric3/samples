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
package org.fabric3.samples.bigbank.loan.expiration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.oasisopen.sca.annotation.ManagedTransaction;
import org.oasisopen.sca.annotation.Scope;

import org.fabric3.api.annotation.Producer;
import org.fabric3.samples.bigbank.api.channel.LoanChannel;

/**
 * A clustered singleton that checks loan application expiration.
 *
 * @version $Rev$ $Date$
 */
@Scope("DOMAIN")
@ManagedTransaction
public class ExpirationTimer implements Runnable {
    private LoanChannel channel;
    private EntityManager em;

    @PersistenceContext(name = "loanApplicationEmf", unitName = "loanApplication")
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public ExpirationTimer(@Producer("loanChannel") LoanChannel channel) {
        this.channel = channel;
    }

    public void run() {
        checkPendingExpiration();
        checkExpired();
    }


    private void checkPendingExpiration() {

    }

    private void checkExpired() {
//        List<LoanRecord> records = em.createQuery("").getResultList();
//        for (LoanRecord record : records) {
//            channel.publish(new ApplicationExpired());
//        }
    }

}

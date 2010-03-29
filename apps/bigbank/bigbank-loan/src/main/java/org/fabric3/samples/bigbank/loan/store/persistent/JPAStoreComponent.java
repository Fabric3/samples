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
package loanapp.store.persistent;

import loanapp.domain.LoanRecord;
import loanapp.store.ApplicationNotFoundException;
import loanapp.store.StoreException;
import loanapp.store.StoreService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.fabric3.api.annotation.transaction.ManagedTransaction;

/**
 * Demonstrates using JPA persistence. By default, the persistence context is transaction-scoped. As this component
 * implementation requires managed transactions, operations will be invoked in the context of a transaction resulting
 * in persistence context changes being written to the database when the transaction completes.
 *
 * @version $Revision: 8744 $ $Date: 2010-03-25 19:43:45 +0100 (Thu, 25 Mar 2010) $
 */
@ManagedTransaction
public class JPAStoreComponent implements StoreService {
    private EntityManager em;

    @PersistenceContext(name = "loanApplicationEmf", unitName = "loanApplication")
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public void save(LoanRecord record) throws StoreException {
        try {
            em.persist(record);
        } catch (PersistenceException e) {
            throw new StoreException(e);
        }
    }

    public void update(LoanRecord record) throws StoreException {
        try {
            em.merge(record);
        } catch (PersistenceException e) {
            throw new StoreException(e);
        }
    }

    public void remove(long id) throws StoreException {
        LoanRecord application = em.find(LoanRecord.class, id);
        if (application == null) {
            throw new ApplicationNotFoundException("Loan application not found: " + id);
        }
        try {
            em.remove(application);
        } catch (PersistenceException e) {
            throw new StoreException(e);
        }
    }

    public LoanRecord find(long id) throws StoreException {
        try {
            Query query = em.createQuery("SELECT r FROM LoanRecord r WHERE r.id = :number");
            query.setParameter("number", id);
            return (LoanRecord) query.getSingleResult();
        } catch (PersistenceException e) {
            throw new StoreException(e);
        }
    }

    public LoanRecord findBySSN(String ssn) throws StoreException {
        try {
            Query query = em.createQuery("SELECT r FROM LoanRecord r WHERE r.ssn = :ssn");
            query.setParameter("ssn", ssn);
            return (LoanRecord) query.getSingleResult();
        } catch (PersistenceException e) {
            throw new StoreException(e);
        }
    }
}

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

import java.util.List;

import org.fabric3.samples.bigbank.api.loan.LoanException;
import org.fabric3.samples.bigbank.api.message.LoanApplication;
import org.fabric3.samples.bigbank.api.message.LoanStatus;

/**
 * Coordinator that handles processing for loan terms that have been accepted by applicants. If a loan is accepted, an
 * appraisal will be ordered and a funding date scheduled once the appraisal has been received and approved.
 *
 * @version $Revision$ $Date$
 */
public interface AcceptanceCoordinator {
    /**
     * Returns the terms of a loan.
     *
     * @param id the loan id
     * @return the loan application
     * @throws LoanException if an exception during acceptance was encountered. Subtypes including
     *                       LoanNotFoundException and LoanNotApprovedException may be thrown.
     */
    LoanApplication retrieve(long id) throws LoanException;

    /**
     * Accepts the terms of a loan.
     *
     * @param id   the loan id
     * @param type the type of option selected
     * @throws LoanException if an exception during acceptance was encountered.
     */
    void accept(long id, String type) throws LoanException;

    /**
     * Declines the terms of a loan.
     *
     * @param id the loan id
     * @throws LoanException if an exception during acceptance was encountered. Subtypes including
     *                       LoanNotFoundException and LoanNotApprovedException may be thrown.
     */
    void decline(long id) throws LoanException;

    /**
     * Returns active loan applications for the current user with the given status.
     *
     * @param status the loan status
     * @return all active loan applications for the user
     */
    List<LoanApplication> retrieveApplications(int status);

    /**
     * Returns the status of all active loan applications for the current user.
     *
     * @return the status of all active loan applications for the user
     */
    List<LoanStatus> getLoanStatus();

}

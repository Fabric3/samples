/*
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
package org.fabric3.samples.bigbank.api.loan;

import loanapp.api.message.LoanApplication;
import loanapp.api.message.LoanRequest;
import loanapp.api.message.OptionSelection;
import org.fabric3.samples.bigbank.api.loan.LoanException;
import org.oasisopen.sca.annotation.Remotable;

/**
 * Service responsible for processing a loan application.
 *
 * @version $Rev: 8744 $ $Date: 2010-03-25 19:43:45 +0100 (Thu, 25 Mar 2010) $
 */
@Remotable
public interface LoanService {

    /**
     * Initiates a loan application.
     *
     * @param request the loan data
     * @return the loan tracking number
     * @throws org.fabric3.samples.bigbank.api.loan.LoanException if an error initiating the application occurs
     */
    long apply(LoanRequest request) throws LoanException;

    /**
     * Returns an in-process loan application.
     *
     * @param id the loan tracking number
     * @return the loan application
     * @throws org.fabric3.samples.bigbank.api.loan.LoanException if a retrieval exception was encountered. For example, LoanApplicationNotFoundException.
     */
    LoanApplication retrieve(long id) throws LoanException;

    /**
     * Declines the terms of a loan application.
     *
     * @param id the loan tracking number
     * @throws org.fabric3.samples.bigbank.api.loan.LoanException if an error declining the loan occurs
     */
    void decline(long id) throws LoanException;

    /**
     * Accepts the terms of a loan application.
     *
     * @param selection contains the loan tracking number and selected terms
     * @throws org.fabric3.samples.bigbank.api.loan.LoanException if an error accepting the loan occurs
     */
    void accept(OptionSelection selection) throws LoanException;

}
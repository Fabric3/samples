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
package org.fabric3.samples.bigbank.api.loan;

import org.fabric3.samples.bigbank.api.message.LoanApplication;
import org.fabric3.samples.bigbank.api.message.LoanApplicationStatus;

/**
 * Service responsible for receiving a loan application.
 *
 * @version $Rev$ $Date$
 */
public interface LoanService {

    /**
     * Submits a loan application.
     *
     * @param application the loan application
     * @return the tracking number
     */
    String apply(LoanApplication application);

    /**
     * Returns the current status of a loan application.
     *
     * @param trackingNumber the loan tracking number
     * @return the current application status
     */
    LoanApplicationStatus getStatus(String trackingNumber);

}
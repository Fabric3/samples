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
package org.fabric3.samples.bigbank.api.event;

/**
 * Denotes a loan application that has been approved, priced and as ready for the applicant to accept.
 *
 * @version $Rev$ $Date$
 */
public class ApplicationReady extends ApplicationEvent {
    private static final long serialVersionUID = 6913418948989296728L;
    private double amount;
    private int approvalType;

    public ApplicationReady(long loanId, double amount, int approvalType) {
        super(loanId);
        this.amount = amount;
        this.approvalType = approvalType;
    }

    public double getAmount() {
        return amount;
    }

    public int getApprovalType() {
        return approvalType;
    }
}

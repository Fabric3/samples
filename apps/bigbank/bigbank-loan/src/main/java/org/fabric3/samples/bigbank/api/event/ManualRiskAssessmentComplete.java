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
 * Issued when risk assessment on an application is complete.
 *
 * @version $Rev$ $Date$
 */
public class ManualRiskAssessmentComplete extends ApplicationEvent {
    private static final long serialVersionUID = 1427555176373119897L;
    private boolean approved;
    private int factor;

    public ManualRiskAssessmentComplete() {
    }

    public ManualRiskAssessmentComplete(long loanId, boolean approved, int factor) {
        super(loanId);
        this.approved = approved;
        this.factor = factor;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public int getRiskFactor() {
        return factor;
    }

    public void setLoanId(long loanId) {
        super.setLoanId(loanId);
    }
}

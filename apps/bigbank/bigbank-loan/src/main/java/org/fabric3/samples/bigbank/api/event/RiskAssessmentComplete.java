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

import org.fabric3.samples.bigbank.services.risk.RiskReason;

/**
 * Issued when risk assessment on an application is complete.
 *
 * @version $Rev$ $Date$
 */
public class RiskAssessmentComplete extends ApplicationEvent {
    private static final long serialVersionUID = 1427555176373119897L;
    public static final RiskReason[] EMPTY = new RiskReason[0];
    public static final int APPROVE = 1;
    public static final int REJECT = -1;
    private int decision;
    private int factor;
    private RiskReason[] reasons = EMPTY;

    public RiskAssessmentComplete(long loanId, int decision, int factor, RiskReason[] reasons) {
        super(loanId);
        this.decision = decision;
        this.factor = factor;
        this.reasons = reasons;
    }

    public int getRiskFactor() {
        return factor;
    }

    public boolean isApproved() {
        return decision == APPROVE;
    }

    public RiskReason[] getReasons() {
        return reasons;
    }

}

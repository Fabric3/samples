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
package org.fabric3.samples.bigbank.risk;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Represents the risk associated with a loan application calculated by the {@link RiskService}.
 *
 * @version $Revision$ $Date$
 */
public class RiskAssessmentResponse implements Serializable {
    private static final long serialVersionUID = 1427555176373119897L;

    public enum Decision {
        APPROVED, REJECTED
    }

    private Decision decision;
    private List<RiskReason> reasons = Collections.emptyList();

    public RiskAssessmentResponse() {
    }

    public RiskAssessmentResponse(Decision decision, List<RiskReason> reasons) {
        this.decision = decision;
        this.reasons = reasons;
    }

    public RiskAssessmentResponse(Decision decision) {
        this.decision = decision;
    }

    public Decision getDecision() {
        return decision;
    }

    public List<RiskReason> getReasons() {
        return reasons;
    }


}

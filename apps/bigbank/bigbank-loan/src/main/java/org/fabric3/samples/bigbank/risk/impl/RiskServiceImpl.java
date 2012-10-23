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
package org.fabric3.samples.bigbank.risk.impl;

import java.util.Collections;
import java.util.List;

import org.oasisopen.sca.annotation.Scope;

import org.fabric3.api.annotation.monitor.Monitor;
import org.fabric3.samples.bigbank.risk.RiskAssessmentRequest;
import org.fabric3.samples.bigbank.risk.RiskAssessmentResponse;
import org.fabric3.samples.bigbank.risk.RiskService;
import org.fabric3.samples.bigbank.risk.RiskReason;

/**
 * Implementation that performs risk assessment based on an applicant's credit score and loan amount.
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
public class RiskServiceImpl implements RiskService {
    private RiskMonitor monitor;

    public RiskServiceImpl(@Monitor RiskMonitor monitor) {
        this.monitor = monitor;
    }

    public RiskAssessmentResponse assessRisk(RiskAssessmentRequest request) {
        int tier = request.getTier();
        monitor.assessmentCompleted();
        if (tier > 1) {
            List<RiskReason> reasons = Collections.singletonList(new RiskReason(1, "Low credit rating"));
            return new RiskAssessmentResponse(RiskAssessmentResponse.Decision.REJECTED, reasons);
        }
        return new RiskAssessmentResponse(RiskAssessmentResponse.Decision.APPROVED);
    }


}

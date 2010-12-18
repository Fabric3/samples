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
package org.fabric3.samples.bigbank.services.risk.impl;

import java.util.ArrayList;
import java.util.List;

import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Scope;

import org.fabric3.api.annotation.monitor.Monitor;
import org.fabric3.samples.bigbank.services.risk.RiskAssessmentRequest;
import org.fabric3.samples.bigbank.services.risk.RiskAssessmentResponse;
import org.fabric3.samples.bigbank.services.risk.RiskAssessmentService;
import org.fabric3.samples.bigbank.services.risk.RiskReason;

/**
 * Implementation that performs risk assessment based on an applicant's credit score and loan amount.
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
public class RiskAssessmentComponent implements RiskAssessmentService {
    private RiskAssessmentMonitor monitor;
    private double ratioMinimum = .10;
    private double ratioFavorableMinimum = .05;

    @Property(required = false)
    public void setRatioMinimum(double ratioMinimum) {
        this.ratioMinimum = ratioMinimum;
    }

    @Property(required = false)
    public void setRatioFavorableMinimum(double ratioFavorableMinimum) {
        this.ratioFavorableMinimum = ratioFavorableMinimum;
    }

    public RiskAssessmentComponent(@Monitor RiskAssessmentMonitor monitor) {
        this.monitor = monitor;
    }

    public RiskAssessmentResponse assessRisk(RiskAssessmentRequest request) {
        int score = request.getCreditScore();
        int factor = 100;
        int decision;
        List<RiskReason> reasons = new ArrayList<RiskReason>();
        if (score < 580) {
            // reject outright
            decision = RiskAssessmentResponse.REJECTED;
            RiskReason reason = new RiskReason("Poor credit history");
            reasons.add(reason);
        } else if (score >= 580 && score < 620) {
            // send for manual approval
            // TODO fire manual approval event
            factor = 50;
            decision = RiskAssessmentResponse.MANUAL_APPROVAL;
        } else if (score >= 620 && score < 720) {
            if (!checkAcceptableRatio(request, ratioMinimum)) {
                reasons.add(new RiskReason("Down payment was too little"));
                decision = RiskAssessmentResponse.REJECTED;
            } else {
                factor = 50;
                decision = RiskAssessmentResponse.APPROVED;
            }
        } else if (score >= 620 && score < 720) {
            // favorable terms
            if (!checkAcceptableRatio(request, ratioFavorableMinimum)) {
                reasons.add(new RiskReason("Down payment was too little"));
                decision = RiskAssessmentResponse.REJECTED;
            } else {
                factor = 0;
                decision = RiskAssessmentResponse.APPROVED;
            }
        } else {
            // favorable terms
            if (!checkAcceptableRatio(request, ratioFavorableMinimum)) {
                reasons.add(new RiskReason("Down payment was too little"));
                decision = RiskAssessmentResponse.REJECTED;
            } else {
                factor = 0;
                decision = RiskAssessmentResponse.APPROVED;
            }
        }
        monitor.assessmentCompleted();
        long id = request.getId();
        if (request.getDownPayment() == 3333) {
            return new RiskAssessmentResponse(id, RiskAssessmentResponse.MANUAL_APPROVAL, factor, reasons);
        }
        return new RiskAssessmentResponse(id, decision, factor, reasons);
    }

    private boolean checkAcceptableRatio(RiskAssessmentRequest request, double floor) {
        double ratio = request.getDownPayment() / request.getAmount();
        return ratio >= floor;
    }
}

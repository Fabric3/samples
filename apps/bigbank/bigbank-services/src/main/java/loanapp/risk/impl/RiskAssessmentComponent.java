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
package loanapp.risk.impl;

import loanapp.risk.RiskAssessmentCallback;
import loanapp.risk.RiskAssessmentService;
import loanapp.risk.RiskReason;
import loanapp.risk.RiskRequest;
import loanapp.risk.RiskResponse;
import org.oasisopen.sca.annotation.Callback;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Scope;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation that performs risk assesment based on an applicant's credit score and loan amount.
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
public class RiskAssessmentComponent implements RiskAssessmentService {
    private RiskAssessmentCallback callback;
    private double ratioMinimum;

    public RiskAssessmentComponent(@Property(name = "ratioMinimum", required = true) Double ratioMinimum) {
        this.ratioMinimum = ratioMinimum;
    }

    @Callback
    public void setCallback(RiskAssessmentCallback callback) {
        this.callback = callback;
    }

    public void assessRisk(RiskRequest request) {
        System.out.println("RiskAssessmentService: Calculating risk");
        int score = request.getCreditScore();
        int factor = 0;
        int decision;
        List<RiskReason> reasons = new ArrayList<RiskReason>();
        if (score < 700) {
            factor += 10;
            reasons.add(new RiskReason("Poor credit history"));
        }
        double ratio = request.getDownPayment() / request.getAmount();
        if (ratio < ratioMinimum) {
            // less than a minimum percentage down, so assign it the highest risk
            factor += 15;
            reasons.add(new RiskReason("Downpayment was too little"));
            reasons.add(new RiskReason("Suspect credit history"));
        }
        if (factor > 24) {
            decision = RiskResponse.REJECT;
        } else {
            decision = RiskResponse.APPROVE;
        }
        RiskResponse result = new RiskResponse(request.getId(), decision, factor, reasons.toArray(new RiskReason[reasons.size()]));

        callback.onAssessment(result);
    }
}

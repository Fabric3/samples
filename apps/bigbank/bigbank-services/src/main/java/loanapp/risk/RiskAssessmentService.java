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
package loanapp.risk;

import org.oasisopen.sca.annotation.Callback;
import org.oasisopen.sca.annotation.OneWay;
import org.oasisopen.sca.annotation.Remotable;

/**
 * Performs risk assessment against a loan application.
 *
 * @version $Rev$ $Date$
 */
@Remotable
@Callback(RiskAssessmentCallback.class)
public interface RiskAssessmentService {

    /**
     * Perform the risk assessment.
     *
     * @param request the request data
     */
    @OneWay
    void assessRisk(RiskRequest request);
}

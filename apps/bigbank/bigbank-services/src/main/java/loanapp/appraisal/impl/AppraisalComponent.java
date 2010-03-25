/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
package loanapp.appraisal.impl;

import org.oasisopen.sca.annotation.Callback;
import org.oasisopen.sca.annotation.OneWay;
import org.oasisopen.sca.annotation.Scope;

import java.util.Date;

import loanapp.appraisal.AppraisalService;
import loanapp.appraisal.AppraisalCallback;
import loanapp.appraisal.AppraisalRequest;
import loanapp.appraisal.AppraisalSchedule;
import loanapp.appraisal.AppraisalResult;

/**
 * @version $Revision$ $Date$
 */
@Scope("COMPOSITE")
public class AppraisalComponent implements AppraisalService {
    private AppraisalCallback callback;

    @Callback
    public void setCallback(AppraisalCallback callback) {
        this.callback = callback;
    }

    @OneWay
    public void appraise(AppraisalRequest request) {
        Date date = new Date(System.currentTimeMillis() + 1000);
        AppraisalSchedule schedule = new AppraisalSchedule(request.getId(), date);
        callback.schedule(schedule);
        AppraisalResult result = new AppraisalResult(request.getId(), AppraisalResult.APPROVED, new String[0]);
        callback.appraisalCompleted(result);
    }
}

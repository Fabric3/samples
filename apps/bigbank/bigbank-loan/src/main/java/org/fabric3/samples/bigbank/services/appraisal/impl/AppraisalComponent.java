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
package org.fabric3.samples.bigbank.services.appraisal.impl;

import java.util.Date;

import org.oasisopen.sca.annotation.OneWay;
import org.oasisopen.sca.annotation.Scope;

import org.fabric3.api.annotation.Producer;
import org.fabric3.api.annotation.monitor.Monitor;
import org.fabric3.samples.bigbank.api.channel.ApplicationEventChannel;
import org.fabric3.samples.bigbank.api.event.AppraisalResult;
import org.fabric3.samples.bigbank.api.event.AppraisalScheduled;
import org.fabric3.samples.bigbank.services.appraisal.AppraisalRequest;
import org.fabric3.samples.bigbank.services.appraisal.AppraisalService;

/**
 * @version $Revision$ $Date$
 */
@Scope("COMPOSITE")
public class AppraisalComponent implements AppraisalService {
    private ApplicationEventChannel loanChannel;
    private AppraisalMonitor monitor;

    public AppraisalComponent(@Producer("loanChannel") ApplicationEventChannel loanChannel, @Monitor AppraisalMonitor monitor) {
        this.loanChannel = loanChannel;
        this.monitor = monitor;
    }

    @OneWay
    public void appraise(AppraisalRequest request) {
        Date date = new Date(System.currentTimeMillis() + 1000);
        long id = request.getId();
        AppraisalScheduled scheduled = new AppraisalScheduled(id, date);
        loanChannel.publish(scheduled);
        AppraisalResult result = new AppraisalResult(request.getId(), AppraisalResult.APPROVED, new String[0]);
        loanChannel.publish(result);
        monitor.appraisalComplete(id);
    }
}

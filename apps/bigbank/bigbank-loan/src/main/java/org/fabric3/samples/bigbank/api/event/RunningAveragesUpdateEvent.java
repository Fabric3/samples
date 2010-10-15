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
 * The root statistics event type.
 *
 * @version $Rev$ $Date$
 */
public class RunningAveragesUpdateEvent extends StatisticsUpdateEvent {
    private static final long serialVersionUID = 2251838095091160041L;

    private double requestAmount = -1;
    private double approvalAmount = -1;
    private long timeToManualApproval = -1;
    private long timeToAutomatedApproval = -1;

    public RunningAveragesUpdateEvent() {
    }

    public double getRequestAmount() {
        return requestAmount;
    }

    public void setRequestAmount(double amount) {
        this.requestAmount = amount;
    }

    public double getApprovalAmount() {
        return approvalAmount;
    }

    public void setApprovalAmount(double approvalAmount) {
        this.approvalAmount = approvalAmount;
    }

    public long getTimeToManualApproval() {
        return timeToManualApproval;
    }

    public void setTimeToManualApproval(long timeToManualApproval) {
        this.timeToManualApproval = timeToManualApproval;
    }

    public long getTimeToAutomatedApproval() {
        return timeToAutomatedApproval;
    }

    public void setTimeToAutomatedApproval(long timeToAutomatedApproval) {
        this.timeToAutomatedApproval = timeToAutomatedApproval;
    }
}

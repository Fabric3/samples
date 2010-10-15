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
package org.fabric3.samples.bigbank.api.domain;

import javax.persistence.Entity;

import org.fabric3.samples.bigbank.api.Versionable;

/**
 * A persistent loan application.
 *
 * @version $Revision$ $Date$
 */
@Entity
public class ApplicationStatistics extends Versionable {
    private static final long serialVersionUID = -7369816219790607261L;

    private long loanId;
    private long receivedTimestamp = -1;
    private long approvedTimestamp = -1;
    private long rejectedTimestamp = -1;
    private long expiredTimestamp = -1;
    private long readyTimestamp = -1;
    private long optionSelectedTimestamp = -1;
    private long appraisalScheduledTimestamp = -1;
    private long fundedTimestamp = -1;


    public long getLoanId() {
        return loanId;
    }

    public void setLoanId(long loanId) {
        this.loanId = loanId;
    }

    public long getReceivedTimestamp() {
        return receivedTimestamp;
    }

    public void setReceivedTimestamp(long receivedTimestamp) {
        this.receivedTimestamp = receivedTimestamp;
    }

    public long getApprovedTimestamp() {
        return approvedTimestamp;
    }

    public void setApprovedTimestamp(long approvedTimestamp) {
        this.approvedTimestamp = approvedTimestamp;
    }

    public long getRejectedTimestamp() {
        return rejectedTimestamp;
    }

    public void setRejectedTimestamp(long rejectedTimestamp) {
        this.rejectedTimestamp = rejectedTimestamp;
    }

    public long getExpiredTimestamp() {
        return expiredTimestamp;
    }

    public void setExpiredTimestamp(long expiredTimestamp) {
        this.expiredTimestamp = expiredTimestamp;
    }

    public long getReadyTimestamp() {
        return readyTimestamp;
    }

    public void setReadyTimestamp(long readyTimestamp) {
        this.readyTimestamp = readyTimestamp;
    }

    public long getOptionSelectedTimestamp() {
        return optionSelectedTimestamp;
    }

    public void setOptionSelectedTimestamp(long optionSelectedTimestamp) {
        this.optionSelectedTimestamp = optionSelectedTimestamp;
    }

    public long getAppraisalScheduledTimestamp() {
        return appraisalScheduledTimestamp;
    }

    public void setAppraisalScheduledTimestamp(long appraisalScheduledTimestamp) {
        this.appraisalScheduledTimestamp = appraisalScheduledTimestamp;
    }

    public long getFundedTimestamp() {
        return fundedTimestamp;
    }

    public void setFundedTimestamp(long fundedTimestamp) {
        this.fundedTimestamp = fundedTimestamp;
    }
}

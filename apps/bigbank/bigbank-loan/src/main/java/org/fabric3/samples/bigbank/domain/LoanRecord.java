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
package org.fabric3.samples.bigbank.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.fabric3.samples.bigbank.api.Versionable;

/**
 * A persistent loan application.
 *
 * @version $Revision$ $Date$
 */
@Entity
public class LoanRecord extends Versionable {
    private static final long serialVersionUID = -5710340587799398147L;

    private LoanStatus status;
    private long timestamp;

    private String ein;
    private double amount;
    private RiskInfo riskInfo;
    private String trackingNumber;

    public String getEin() {
        return ein;
    }

    public void setEin(String ein) {
        this.ein = ein;
    }

    /**
     * Returns the loan status.
     *
     * @return the loan status
     */
    public LoanStatus getStatus() {
        return status;
    }

    /**
     * Sets the loan status.
     *
     * @param status the loan status
     */
    public void setStatus(LoanStatus status) {
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns the loan amount.
     *
     * @return the loan amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the loan amount.
     *
     * @param amount the loan amount
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Returns the applicant's risk assessment.
     *
     * @return the applicant's risk assessment
     */
    @OneToOne(cascade = CascadeType.ALL)
    public RiskInfo getRiskInfo() {
        return riskInfo;
    }

    /**
     * Sets the applicant's risk assessment.
     *
     * @param assessment the applicant's risk assessment
     */
    public void setRiskInfo(RiskInfo assessment) {
        this.riskInfo = assessment;
    }


    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }
}

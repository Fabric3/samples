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
package org.fabric3.samples.bigbank.loan.domain;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
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

    private String username;
    private long loanNumber;
    private long expiration;
    private int status;
    private String ssn;
    private String email;
    private double amount;
    private double downPayment;
    private String typeSelected;
    private PropertyInfo propertyInfo;
    private RiskInfo riskInfo;
    private List<TermInfo> terms;
    private int creditScore;

    public LoanRecord() {
    }

    public long getLoanNumber() {
        return loanNumber;
    }

    public void setLoanNumber(long loanNumber) {
        this.loanNumber = loanNumber;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the loan status as defined in {@link org.fabric3.samples.bigbank.api.message.LoanStatus}.
     *
     * @return the loan status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the loan status as defined in {@link org.fabric3.samples.bigbank.api.message.LoanStatus}.
     *
     * @param status the loan status
     */
    public void setStatus(int status) {
        this.status = status;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
     * Returns the loan down payment amount.
     *
     * @return the loan down payment amount
     */
    public double getDownPayment() {
        return downPayment;
    }

    /**
     * Sets the loan down payment amount.
     *
     * @param downPayment loan down payment amount
     */
    public void setDownPayment(double downPayment) {
        this.downPayment = downPayment;
    }

    @OneToOne(cascade = CascadeType.ALL)
    public PropertyInfo getPropertyInfo() {
        return propertyInfo;
    }

    public void setPropertyInfo(PropertyInfo propertyInfo) {
        this.propertyInfo = propertyInfo;
    }

    /**
     * Returns the applicant's credit score
     *
     * @return the applicant's credit score
     */
    public int getCreditScore() {
        return creditScore;
    }

    /**
     * Sets the applicant's credit score
     *
     * @param creditScore the applicant's credit score
     */
    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
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


    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public void setTerms(List<TermInfo> terms) {
        this.terms = terms;
    }

    @OneToMany(cascade = CascadeType.ALL)
    public List<TermInfo> getTerms() {
        return terms;
    }

    public String getTypeSelected() {
        return typeSelected;
    }

    public void setTypeSelected(String typeSelected) {
        this.typeSelected = typeSelected;
    }

}

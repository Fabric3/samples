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
package org.fabric3.samples.bigbank.api.loan;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Encapsulates information for a new loan application.
 *
 * @version $Rev$ $Date$
 */
@XmlRootElement(namespace = "http://loan.api.bigbank.samples.fabric3.org/")
public class LoanApplication implements Serializable {
    private static final long serialVersionUID = -6182280155050660264L;

    private String clientCorrelation;
    private String notificationAddress;
    private String ein;
    private double amount;

    public String getClientCorrelation() {
        return clientCorrelation;
    }

    public void setClientCorrelation(String clientCorrelation) {
        this.clientCorrelation = clientCorrelation;
    }

    public String getEin() {
        return ein;
    }

    public void setEin(String ein) {
        this.ein = ein;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getNotificationAddress() {
        return notificationAddress;
    }

    public void setNotificationAddress(String notificationAddress) {
        this.notificationAddress = notificationAddress;
    }
}
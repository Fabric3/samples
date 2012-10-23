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
package org.fabric3.samples.bigbank.risk;

import java.io.Serializable;

/**
 * @version $Revision$ $Date$
 */
public class RiskAssessmentRequest implements Serializable {
    private static final long serialVersionUID = -5185791927524383209L;
    private long ein;
    private int tier;
    private double amount;

    public RiskAssessmentRequest(String ein, int tier, double amount) {
        this.tier = tier;
        this.amount = amount;
    }

    public long getEin() {
        return ein;
    }

    public int getTier() {
        return tier;
    }

    public double getAmount() {
        return amount;
    }

}

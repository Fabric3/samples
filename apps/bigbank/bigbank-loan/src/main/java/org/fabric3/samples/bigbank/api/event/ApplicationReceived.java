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
 * Denotes a new loan application that has been received.
 *
 * @version $Rev$ $Date$
 */
public class ApplicationReceived extends ApplicationEvent {
    private static final long serialVersionUID = -3179786299288877078L;
    private double amount;

    public ApplicationReceived(long loanId, double amount) {
        super(loanId);
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }
}

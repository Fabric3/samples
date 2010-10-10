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
package org.fabric3.samples.bigbank.services.credit.impl;

import java.util.List;

import org.oasisopen.sca.annotation.Reference;

import org.fabric3.samples.bigbank.services.credit.CreditBureau;
import org.fabric3.samples.bigbank.services.credit.CreditScore;
import org.fabric3.samples.bigbank.services.credit.CreditService;

/**
 * @version $Rev$ $Date$
 */
public class ConsolidatedCreditComponent implements CreditService {
    private List<CreditBureau> bureaus;

    @Reference
    public void setCreditServices(List<CreditBureau> bureaus) {
        this.bureaus = bureaus;
    }

    public CreditScore score(String ssn) {
        System.out.println("CreditService: Calculating credit score");

        int size = bureaus.size();
        int total = 0;
        for (CreditBureau bureau : bureaus) {
            total = bureau.score(ssn);
        }
        int value = total / size;
        return new CreditScore(ssn, value);
        // TODO fire audit
    }
}
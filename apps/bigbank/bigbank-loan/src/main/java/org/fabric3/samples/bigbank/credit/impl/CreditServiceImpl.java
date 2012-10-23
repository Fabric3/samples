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
package org.fabric3.samples.bigbank.credit.impl;

import java.util.List;

import org.oasisopen.sca.annotation.Reference;

import org.fabric3.api.annotation.monitor.Monitor;
import org.fabric3.samples.bigbank.credit.CreditBureau;
import org.fabric3.samples.bigbank.credit.CreditScore;
import org.fabric3.samples.bigbank.credit.CreditService;

/**
 * Implementation that delegates to multiple {@link CreditBureau} services.
 * <p/>
 * Demonstrates the use of multiplicity references - i.e. a collection of injected services
 *
 * @version $Rev$ $Date$
 */
public class CreditServiceImpl implements CreditService {
    private CreditMonitor monitor;

    @Reference
    protected List<CreditBureau> bureaus;

    public CreditServiceImpl(@Monitor CreditMonitor monitor) {
        this.monitor = monitor;
    }

    public CreditScore score(String ein) {
        int size = bureaus.size();
        int total = 0;

        // average the scores
        for (CreditBureau bureau : bureaus) {
            total = total + bureau.score(ein);
        }
        int value = total / size;
        CreditScore score = new CreditScore(ein, value);
        monitor.creditCheck();
        return score;
    }
}
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
package org.fabric3.samples.bigbank.rate.impl;

import org.fabric3.api.annotation.monitor.Monitor;
import org.fabric3.samples.bigbank.credit.CreditScore;
import org.fabric3.samples.bigbank.credit.CreditService;
import org.fabric3.samples.bigbank.rate.Rating;
import org.fabric3.samples.bigbank.rate.RatingRequest;
import org.fabric3.samples.bigbank.rate.RatingService;
import org.fabric3.samples.bigbank.rate.RatingServiceCallback;

import org.oasisopen.sca.annotation.Callback;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;

/**
 * This implementation demonstrates the use of asynchronous, non-blocking (@OneWay) operations and how to return a response to the initiating client.
 * Fabric3 will inject the @Callback field with a proxy to the client that can be used to return a response.
 * <p/>
 * Asynchronous operations are useful when a response may take an extended period of time and resources such as threads should not be blocked
 * waiting.
 * <p/>
 * If deployed to a single VM environment, asynchronous communications will be performed in-memory using different threads. If deployed to a cluster
 * setup where zone1 and zone2 composites are provisioned to different runtime instances, communications will be performed over JMS or ZeroMQ
 * depending on the cluster setup. Note, however, application code remains unchanged as remote communications are abstracted
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
public class RatingComponent implements RatingService {
    private CreditService creditService;
    private RatingMonitor monitor;

    @Callback
    protected RatingServiceCallback callback;

    public RatingComponent(@Reference(name = "CreditService") CreditService creditService, @Monitor RatingMonitor monitor) {
        this.creditService = creditService;
        this.monitor = monitor;
    }

    public void rate(RatingRequest request) {
        String ein = request.getEin();
        long id = request.getCorrelationId();

        CreditScore score = creditService.score(ein);

        Rating rating;
        if (score.getScore() < 700) {
            rating = new Rating(ein, id, 2);
        } else {
            rating = new Rating(ein, id, 1);
        }

        monitor.ratingCompleted();
        callback.onResults(rating);
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
package org.fabric3.samples.channel;

import org.fabric3.api.ChannelEvent;
import org.fabric3.api.annotation.Consumer;
import org.fabric3.api.annotation.model.Component;
import org.fabric3.api.annotation.monitor.Monitor;
import org.fabric3.api.annotation.scope.Composite;

/**
 * Consumer that demonstrates how to implement worker pools (i.e. one consumer processes an event) with ring buffer channels and a modulo operation.
 */
@Composite
@Component
public class PooledWorker0 {
    private static final int ORDINAL = 0;
    private static final int NUMBER_OF_CONSUMERS = 2;

    @Monitor
    protected SystemMonitor monitor;

    @Consumer (source = "WorkerPoolChannel")
    public void onEvent(ChannelEvent event) {
        if ((event.getSequence() % NUMBER_OF_CONSUMERS) != ORDINAL) {
            // ignore the event if it is not for this consumer
            return;
        }
        String message = event.getEvent(String.class);
        monitor.process(ORDINAL, message);
    }
}

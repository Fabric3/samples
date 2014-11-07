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
import org.fabric3.api.annotation.Producer;
import org.fabric3.api.annotation.model.Component;
import org.fabric3.api.annotation.monitor.Monitor;
import org.fabric3.api.annotation.scope.Composite;
import org.fabric3.api.implementation.timer.annotation.Timer;

/**
 * Demonstrates the following channel capabilities:
 * <pre>
 *     - Using high-performance ring-buffer channels based on the Disruptor (http://lmax-exchange.github.io/disruptor/)
 *     - Using ordered (sequenced) consumers to first deserializing a message and store its value for subsequent processing in {@link ChannelEvent}
 *     - Worker pools (an event is processed by one consumer)
 *     - Receiving end batch notifications from {@link ChannelEvent#isEndOfBatch()}
 * </pre>
 */
@Composite
@Timer(repeatInterval = 3000)
@Component
public class ChannelProducer implements Runnable {
    @Monitor
    protected SystemMonitor monitor;

    private int counter;

    @Producer(target = "WorkChannel")
    protected WorkChannel workChannel;

    @Producer(target = "TypedChannel")
    protected TypedChannel typedChannel;

    @Producer(target = "WorkerPoolChannel")
    WorkerPoolChannel workerPoolChannel;

    public void run() {
        monitor.send();

        String message = "This is an event: " + counter++;
        workChannel.send(message.getBytes());  // serialized messages example with ordered processing

        workerPoolChannel.publish(message);    // worker pool example

        Event event = new Event(message);      // typed messages example
        typedChannel.publish(event);

    }
}

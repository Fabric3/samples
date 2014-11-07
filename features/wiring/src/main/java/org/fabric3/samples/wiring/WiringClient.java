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
package org.fabric3.samples.wiring;

import org.fabric3.api.annotation.model.Component;
import org.fabric3.api.annotation.scope.Scopes;
import org.fabric3.api.implementation.timer.annotation.Timer;
import org.fabric3.samples.wiring.pipeline.Pipeline;
import org.fabric3.samples.wiring.pipeline.PipelineMessage;
import org.fabric3.samples.wiring.registry.IntMessage;
import org.fabric3.samples.wiring.registry.Registry;
import org.fabric3.samples.wiring.registry.StringMessage;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;

/**
 * Demonstrates message dispatching using keyed references injected on a {@link Registry} and processing {@link Pipeline}s using an ordered list of processor
 * references.
 */
@Scope(Scopes.COMPOSITE)
@Component
@Timer(repeatInterval = 3000)
public class WiringClient implements Runnable {
    private int counter;

    @Reference
    protected Registry registry;

    @Reference
    protected Pipeline pipeline;

    public void run() {
        int result = counter % 3;
        if (result == 0) {
            pipeline.process(new PipelineMessage("Message " + counter));
        } else if (result == 1) {
            registry.handle(new StringMessage("Test " + counter));
        } else {
            registry.handle(new IntMessage(counter));
        }
        counter++;
    }
}

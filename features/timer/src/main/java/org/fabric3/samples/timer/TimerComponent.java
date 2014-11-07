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
package org.fabric3.samples.timer;

import org.fabric3.api.annotation.model.Component;
import org.fabric3.api.implementation.timer.annotation.Timer;
import org.fabric3.api.implementation.timer.model.TimerType;

/**
 * A simple stateless timer component.
 */
@Timer(type = TimerType.RECURRING)
@Component
public class TimerComponent implements Runnable {

    public void run() {
        System.out.println("Timer fired...");
    }

    /**
     * Called by the runtime to determine when to schedule the next fire event.
     *
     * @return the interval to the next fire event in milliseconds
     */
    public long nextInterval() {
        return 1000;
    }
}

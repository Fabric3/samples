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
package org.fabric3.samples.eventing;

import java.math.BigDecimal;
import java.util.Random;

import org.fabric3.api.annotation.Producer;
import org.fabric3.api.annotation.model.Component;
import org.fabric3.api.annotation.monitor.Info;
import org.fabric3.api.annotation.monitor.Monitor;
import org.fabric3.api.annotation.scope.Composite;
import org.fabric3.api.implementation.timer.annotation.Timer;
import org.fabric3.api.implementation.timer.model.TimerType;

/**
 * A simple stateless timer component that issues buy orders.
 */
@Component
@Composite
@Timer(type = TimerType.RECURRING)
public class BuyTimer implements Runnable {
    private BuyChannel buyChannel;
    private Random generator;
    private BuyMonitor monitor;

    public BuyTimer(@Producer(target = "BuyChannel") BuyChannel buyChannel, @Monitor BuyMonitor monitor) {
        this.buyChannel = buyChannel;
        this.monitor = monitor;
        generator = new Random();
    }

    public void run() {
        double price = generatePrice();
        BuyOrder buyOrder = new BuyOrder(System.currentTimeMillis(), "FOO", price, System.currentTimeMillis() + 10000);
        monitor.message("Buying");
        buyChannel.buy(buyOrder);
    }

    public long nextInterval() {
        return 3000 + (int) (Math.random() * 5000);
    }

    private double generatePrice() {
        double val = generator.nextDouble() * 5 + 70.0;
        BigDecimal bd = new BigDecimal(Double.toString(val));
        return bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public interface BuyMonitor {
        @Info
        void message(String message);
    }

}

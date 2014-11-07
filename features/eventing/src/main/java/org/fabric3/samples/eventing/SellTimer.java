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
import org.fabric3.api.annotation.scope.Composite;
import org.fabric3.api.implementation.timer.annotation.Timer;
import org.fabric3.api.implementation.timer.model.TimerType;

/**
 * A simple stateless timer component that issues sell orders.
 *
 *
 */
@Component
@Composite
@Timer(type = TimerType.RECURRING)
public class SellTimer implements Runnable {
    private SellChannel sellChannel;
    private Random generator;

    public SellTimer(@Producer(target = "SellChannel") SellChannel sellChannel) {
        this.sellChannel = sellChannel;
        generator = new Random();
    }

    public void run() {
        double price = generatePrice();
        SellOrder sellOrder = new SellOrder(System.currentTimeMillis(), "FOO", price);
        System.out.println("Sending sell order: " + sellOrder.getSymbol() + " @ " + sellOrder.getPrice());
        sellChannel.sell(sellOrder);
    }

    public long nextInterval() {
        return 3000 + (int) (Math.random() * 5000);
    }

    private double generatePrice() {
        double val = generator.nextDouble() * 5 + 70.0;
        BigDecimal bd = new BigDecimal(Double.toString(val));
        return bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    
}

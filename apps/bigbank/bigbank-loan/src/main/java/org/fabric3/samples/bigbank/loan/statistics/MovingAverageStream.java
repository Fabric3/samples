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
package org.fabric3.samples.bigbank.loan.statistics;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Calculates the moving average of a data set using a fixed period.
 *
 * @version $Rev$ $Date$
 */
public class MovingAverageStream {
    private int period;
    private double sum;
    private Queue<Double> window = new ConcurrentLinkedQueue<Double>();

    public MovingAverageStream(int period) {
        this.period = period;
    }

    public void write(double num) {
        sum += num;
        window.add(num);
        if (window.size() > period) {
            sum -= window.remove();
        }
    }

    public double readAverage() {
        if (window.isEmpty()) {
            return -1;
        }
        return sum / window.size();
    }

}

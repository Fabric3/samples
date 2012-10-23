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

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Scope;

import org.fabric3.api.annotation.Consumer;
import org.fabric3.api.annotation.Producer;
import org.fabric3.api.annotation.Resource;
import org.fabric3.samples.bigbank.api.event.ApplicationEvent;
import org.fabric3.samples.bigbank.api.event.ApplicationReceived;
import org.fabric3.samples.bigbank.api.event.RunningAveragesUpdateEvent;
import org.fabric3.samples.bigbank.domain.ApplicationStatistics;

/**
 * A consumer that listens on the loan channel for loan application events and publishes running average events to the statistics channel.
 * <p/>
 * This implementation demonstrates how to create an event consumer and producer.
 * <p/>
 * This implementation also demonstrates the use of runtime @Resource injection - the ability to access runtime resources such as the pooled
 * ScheduledExecutorService to schedule a recurring Runnable process.
 * <p/>
 *
 * @version $Rev$ $Date$
 */
@EagerInit
@Scope("COMPOSITE")
public class ApplicationStatisticsComponent {
    @Producer
    protected StatisticsChannel statisticsChannel;

    @Resource
    protected ScheduledExecutorService executorService;

    private StatisticsRunnable runnable;
    private volatile long wait = 10000;

    private MovingAverage amountAverage;
    private MovingAverage approvalAverage;
    private MovingAverage timeToCompleteAutomatedAverage;
    private MovingAverage timeToCompleteManualAverage;

    @Init
    public void init() throws IOException {
        amountAverage = new MovingAverage(4);
        approvalAverage = new MovingAverage(4);
        timeToCompleteAutomatedAverage = new MovingAverage(4);
        timeToCompleteManualAverage = new MovingAverage(4);

        runnable = new StatisticsRunnable();
        executorService.schedule(runnable, wait, TimeUnit.MILLISECONDS);
    }

    @Destroy
    public void destroy() throws IOException {
        if (runnable != null) {
            runnable.stop();
        }
    }

    @Consumer("loanChannel")
    public void onEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReceived) {
            onReceived((ApplicationReceived) event);
        }
    }

    private void onReceived(ApplicationReceived event) {
        ApplicationStatistics statistics = new ApplicationStatistics();
        statistics.setLoanId(event.getLoanId());
        statistics.setReceivedTimestamp(System.currentTimeMillis());
        double amount = event.getAmount();
        amountAverage.write(amount);
    }

    /**
     * Reads moving average and periodically publishes updates to the statistics channel.
     */
    private class StatisticsRunnable implements Runnable {
        private boolean stop;

        public void stop() {
            this.stop = true;
        }

        public void run() {
            RunningAveragesUpdateEvent event = new RunningAveragesUpdateEvent();
            double amount = amountAverage.readAverage();
            event.setRequestAmount(amount);
            amount = approvalAverage.readAverage();
            event.setApprovalAmount(amount);
            event.setTimeToAutomatedApproval((long) timeToCompleteAutomatedAverage.readAverage());
            event.setTimeToManualApproval((long) timeToCompleteManualAverage.readAverage());

            statisticsChannel.update(event);
            if (!stop) {
                // schedule the next read
                executorService.schedule(this, wait, TimeUnit.MILLISECONDS);
            }
        }
    }

}

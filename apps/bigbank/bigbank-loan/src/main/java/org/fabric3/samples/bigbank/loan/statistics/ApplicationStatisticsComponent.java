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
import java.util.concurrent.ExecutorService;

import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Scope;

import org.fabric3.api.annotation.Consumer;
import org.fabric3.api.annotation.Producer;
import org.fabric3.api.annotation.Resource;
import org.fabric3.api.annotation.management.Management;
import org.fabric3.api.annotation.management.ManagementOperation;
import org.fabric3.samples.bigbank.api.event.ApplicationEvent;
import org.fabric3.samples.bigbank.api.event.ApplicationExpired;
import org.fabric3.samples.bigbank.api.event.ApplicationReady;
import org.fabric3.samples.bigbank.api.event.ApplicationReceived;
import org.fabric3.samples.bigbank.api.event.AppraisalScheduled;
import org.fabric3.samples.bigbank.api.event.ManualRiskAssessmentComplete;
import org.fabric3.samples.bigbank.api.event.RunningAverageAmountUpdateEvent;

/**
 * @version $Rev$ $Date$
 */
@EagerInit
@Management
@Scope("COMPOSITE")
public class ApplicationStatisticsComponent {
    private volatile long wait = 10000;

    private MovingAverageStream amountStream;
    private ExecutorService executorService;
    private StatisticsChannel statisticsChannel;
    private StatisticsRunnable runnable;

    @Producer
    public void setStatisticsChannel(StatisticsChannel channel) {
        this.statisticsChannel = channel;
    }

    @Resource(mappedName = "RuntimeThreadPoolExecutor")
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @ManagementOperation(description = "Sets the period to wait between moving average updates")
    public void setWait(long wait) {
        this.wait = wait;
    }

    @Init
    public void init() throws IOException {
        amountStream = new MovingAverageStream(4);
        runnable = new StatisticsRunnable();
        executorService.execute(runnable);
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
        } else if (event instanceof ManualRiskAssessmentComplete) {
            onAssessmentComplete((ManualRiskAssessmentComplete) event);
        } else if (event instanceof ApplicationReady) {
            onApplicationReady((ApplicationReady) event);
        } else if (event instanceof AppraisalScheduled) {
            onAppraisalScheduled((AppraisalScheduled) event);
        } else if (event instanceof ApplicationExpired) {
            onExpired((ApplicationExpired) event);
        }
    }

    private void onAppraisalScheduled(AppraisalScheduled event) {
    }

    private void onReceived(ApplicationReceived event) {
        double amount = event.getRecord().getAmount();
        amountStream.write(amount);
    }

    private void onAssessmentComplete(ManualRiskAssessmentComplete event) {
    }

    private void onApplicationReady(ApplicationReady event) {
    }

    private void onExpired(ApplicationExpired event) {
    }

    /**
     * Reads from a moving average stream. Note this implementation reads using a timeout to avoid tying up threads and allowing other work to be
     * fairly scheduled.
     */
    private class StatisticsRunnable implements Runnable {
        private boolean stop;

        public void stop() {
            this.stop = true;
        }

        public void run() {
            double average = amountStream.readAverage();
            // if the average is less than 0, the read timed out, so only reschedule
            if (average >= 0) {
                statisticsChannel.update(new RunningAverageAmountUpdateEvent(average));
            }
            if (!stop) {
                try {
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                    return;
                }
                // schedule the next read
                executorService.execute(this);
            }
        }
    }

}

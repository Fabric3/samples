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
package org.fabric3.samples.web.calculator;

import java.util.concurrent.atomic.AtomicBoolean;

import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;

import org.fabric3.api.annotation.management.Management;
import org.fabric3.api.annotation.management.ManagementOperation;
import org.fabric3.api.annotation.monitor.Info;
import org.fabric3.api.annotation.monitor.Monitor;


/**
 * Implementation of the CalculatorService.
 * <p/>
 * This implementation also demonstrates the use of Fabric3 management extensions. To turn on auditing, use a JMX client such as JConsole and connect
 * to the runtime hosting the component:
 * <p/>
 * service:jmx:rmi:///jndi/rmi://<ip address of server>:<JMX port>/server
 * <p/>
 * Navigate to fabric3/domain/component/CalculatorService and invoke the audit operation.
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
@Management(description = "A calculator component")
public class CalculatorServiceImpl implements CalculatorService {
    private AddService addService;
    private SubtractService subtractService;
    private MultiplyService multiplyService;
    private DivideService divideService;
    private AtomicBoolean audit = new AtomicBoolean(false);

    @Monitor
    protected TestMonitor monitor;

    /**
     * Creates a calculator component, taking references to dependent services.
     *
     * @param addService      the service for performing addition
     * @param subtractService the service for performing subtraction
     * @param multiplyService the service for performing multiplication
     * @param divideService   the service for performing division
     */
    public CalculatorServiceImpl(@Reference(name = "addService") AddService addService,
                                 @Reference(name = "subtractService") SubtractService subtractService,
                                 @Reference(name = "multiplyService") MultiplyService multiplyService,
                                 @Reference(name = "divideService") DivideService divideService) {
        this.addService = addService;
        this.subtractService = subtractService;
        this.multiplyService = multiplyService;
        this.divideService = divideService;
    }

    public double add(double n1, double n2) {
        if (audit.get()) {
            System.out.println("<<Auditing>> " + n1 + " + " + n2);
        }
        monitor.test(String.format("%s=%s", n1, String.valueOf(122)));
        return addService.add(n1, n2);
    }

    public double subtract(double n1, double n2) {
        if (audit.get()) {
            System.out.println("<<Auditing>> " + n1 + " - " + n2);
        }
        return subtractService.subtract(n1, n2);
    }

    public double multiply(double n1, double n2) {
        if (audit.get()) {
            System.out.println("<<Auditing>> " + n1 + " * " + n2);
        }
        return multiplyService.multiply(n1, n2);
    }

    public double divide(double n1, double n2) {
        if (audit.get()) {
            System.out.println("<<Auditing>> " + n1 + " / " + n2);
        }
        return divideService.divide(n1, n2);
    }


    @ManagementOperation(description = "Start auditing")
    public void startAudit() {
        audit.set(true);
    }

    @ManagementOperation(description = "Stop auditing")
    public void stopAudit() {
        audit.set(false);
    }

    private interface TestMonitor {
        @Info
        void test(String s);
    }
}

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
package org.fabric3.tutorials.ws.calculator;

import org.oasisopen.sca.annotation.Reference;


/**
 * Implementaton of the CalculatorService.
 */
public class CalculatorServiceImpl implements CalculatorService {
    private AddService addService;
    private SubtractService subtractService;
    private MultiplyService multiplyService;
    private DivideService divideService;

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
        return addService.add(n1, n2);
    }

    public double subtract(double n1, double n2) {
        return subtractService.subtract(n1, n2);
    }

    public double multiply(double n1, double n2) {
        return multiplyService.multiply(n1, n2);
    }

    public double divide(double n1, double n2) {
        return divideService.divide(n1, n2);
    }


}

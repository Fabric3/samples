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
package org.fabric3.samples.rs.calculator;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.fabric3.api.annotation.model.Component;
import org.fabric3.api.annotation.model.EndpointUri;
import org.fabric3.api.annotation.scope.Composite;
import org.oasisopen.sca.annotation.Reference;

/**
 * A REST calculator.
 */
@Path("/")
@EndpointUri("calculator")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.TEXT_PLAIN)
@Component
@Composite
public class CalculatorService {

    @Reference
    protected AddService addService;

    @Reference
    protected SubtractService subtractService;

    @Reference
    protected MultiplyService multiplyService;

    @Reference
    protected DivideService divideService;

    @GET
    @Path("/{formula}")
    public String calculate(@PathParam("formula") String formula) {
        formula = formula.replaceAll("\\s+", "");
        String[] tokens = formula.split("[\\+\\-\\*\\\\\\:]");
        if (tokens.length != 2) {
            throw new IllegalArgumentException("Invalid formula: " + formula);
        }
        double operand1 = Double.parseDouble(tokens[0]);
        double operand2 = Double.parseDouble(tokens[1]);
        double result;
        if (formula.indexOf("+") > 0) {
            result = addService.add(operand1, operand2);
        } else if (formula.indexOf("-") > 0) {
            result = subtractService.subtract(operand1, operand2);
        } else if (formula.indexOf("*") > 0) {
            result = multiplyService.multiply(operand1, operand2);
        } else if (formula.indexOf(":") > 0) {
            result = divideService.divide(operand1, operand2);
        } else {
            throw new IllegalArgumentException("Invalid formula: " + formula);
        }

        String resultString = String.valueOf(result);
        System.out.println(String.format("%s=%s", formula, result));
        return resultString;
    }

}

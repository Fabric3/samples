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
package org.fabric3.samples.web.ui;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

import org.fabric3.api.annotation.Target;
import org.fabric3.samples.web.calculator.CalculatorService;
import org.oasisopen.sca.annotation.Reference;

/**
 * Accepts a calculator form submission and forwards the request to the CalculatorService.
 */
@WebServlet("calculatorServlet")
public class CalculatorServlet extends HttpServlet {
    private static final long serialVersionUID = -2731185078362675240L;

    @Reference
    @Target("CalculatorService")
    protected transient CalculatorService calculatorService;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String operationParam = request.getParameter("operation");
        double operand1 = Double.valueOf(request.getParameter("operand1"));
        double operand2 = Double.valueOf(request.getParameter("operand2"));
        String operation;
        double result;
        switch (operationParam) {
            case "add":
                operation = " + ";
                result = calculatorService.add(operand1, operand2);
                break;
            case "subtract":
                operation = " - ";
                result = calculatorService.subtract(operand1, operand2);
                break;
            case "multiply":
                operation = " * ";
                result = calculatorService.multiply(operand1, operand2);
                break;
            case "divide":
                operation = " / ";
                result = calculatorService.divide(operand1, operand2);
                break;
            default:
                throw new ServletException("Unknown operation type");
        }
        Writer out = response.getWriter();
        out.write("<html><head><title>Fabric3 Web Calculator</title></head><body>");
        out.write("<h2>Calculator Result</h2>");
        out.write("<br>" + operand1 + operation + operand2 + " = " + result);
        out.write("</body></html>");
        out.flush();
        out.close();
    }

}

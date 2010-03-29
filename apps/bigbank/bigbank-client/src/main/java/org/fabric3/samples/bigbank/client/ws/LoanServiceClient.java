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
package org.fabric3.samples.bigbank.client.ws;

import org.fabric3.samples.bigbank.client.ws.loan.Address;
import org.fabric3.samples.bigbank.client.ws.loan.LoanRequest;
import org.fabric3.samples.bigbank.client.ws.loan.LoanService;
import org.fabric3.samples.bigbank.client.ws.loan.LoanServiceService;

import javax.xml.namespace.QName;
import java.net.URL;
import java.util.UUID;

/**
 * Demonstrates interacting with the BigBank Loan Service via web services.
 *
 * @version $Rev$ $Date$
 */
public class LoanServiceClient {

    public static void main(String[] args) throws Exception {
        // URL when the loan service is deployed to the single-VM runtime
        // URL url = new URL("http://localhost:8080/loanService?wsdl");
        // URL when loan service deployed in the cluster without a load-balancer
        URL url = new URL("http://localhost:8181/loanService?wsdl");
        QName name = new QName("http://loan.api.bigbank.samples.fabric3.org/", "LoanServiceService");
        LoanServiceService service = new LoanServiceService(url, name);
        LoanService loanService = service.getLoanServicePort();

        // apply for a loan
        LoanRequest request = new LoanRequest();
        request.setAmount(300000);
        request.setDownPayment(15000);
        request.setEmail("foo@bar.com");
        request.setSSN(UUID.randomUUID().toString());
        Address address = new Address();
        address.setCity("San Francisco");
        address.setState("CA");
        address.setStreet("123 Kearney");
        address.setZip(94110);
        request.setPropertyAddress(address);
        long id = loanService.apply(request);


    }
}
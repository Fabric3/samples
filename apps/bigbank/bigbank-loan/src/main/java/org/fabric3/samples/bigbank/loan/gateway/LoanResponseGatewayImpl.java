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
package org.fabric3.samples.bigbank.loan.gateway;

import java.io.IOException;
import java.io.OutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;

import org.fabric3.api.annotation.monitor.Monitor;
import org.fabric3.samples.bigbank.api.loan.LoanApplicationStatus;
import org.fabric3.samples.bigbank.domain.LoanRecord;

/**
 * @version $Rev: 11193 $ $Date: 2012-10-24 11:30:17 +0200 (Wed, 24 Oct 2012) $
 */
@Scope("COMPOSITE")
public class LoanResponseGatewayImpl implements LoanResponseGateway {
    private ResponseQueue responseQueue;
    private ResponseMonitor monitor;
    private JAXBContext context;

    public LoanResponseGatewayImpl(@Reference(name = "responseQueue") ResponseQueue responseQueue, @Monitor ResponseMonitor monitor)
            throws JAXBException {
        this.responseQueue = responseQueue;
        this.monitor = monitor;
        context = JAXBContext.newInstance("org.fabric3.samples.bigbank.api.loan");
    }

    public void completed(LoanRecord record) {
        if ("file".equals(record.getNotificationAddress())) {
            try {
                Marshaller marshaller = context.createMarshaller();
                LoanApplicationStatus status = new LoanApplicationStatus(record.getClientCorrelation(), record.getStatus().toString());
                OutputStream stream = responseQueue.openStream(record.getClientCorrelation() + ".xml");
                marshaller.marshal(status, stream);
                stream.close();
            } catch (JAXBException e) {
                monitor.error("Error sending notification: " + record.getId(), e);
            } catch (IOException e) {
                monitor.error("Error closing stream: " + record.getId(), e);
            }

        }

    }
}

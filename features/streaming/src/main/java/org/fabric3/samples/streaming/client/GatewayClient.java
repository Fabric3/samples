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
package org.fabric3.samples.streaming.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.fabric3.samples.streaming.server.GatewayConstants;
import org.fabric3.samples.streaming.server.GatewayService;

/**
 * Client that demonstrates how to upload data to the {@link GatewayService} via the JAX-RS client API.
 */
public class GatewayClient {
    private static final int RECORDS = 1000;
    private static final String BASE_URL = "http://localhost:8181";

    public static void main(String[] args) throws Exception {
        Client client = ClientBuilder.newClient();
        URI uploadUri = UriBuilder.fromUri(BASE_URL).path("streaming").path("upload").build();

        WebTarget resource = client.target(uploadUri);
        Response response = resource.request().post(Entity.entity(generate(), MediaType.APPLICATION_OCTET_STREAM_TYPE));

        if (response.getStatus() == 204) {
            System.out.println("Upload completed");
        } else {
            System.out.println("Upload error: " + response.getStatus());
        }
    }

    private static InputStream generate() throws XMLStreamException, IOException {

        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
            writer.setDefaultNamespace(GatewayConstants.NAMESPACE);
            writer.writeStartDocument();
            writer.writeStartElement("entries");
            writer.writeAttribute("xmlns", GatewayConstants.NAMESPACE);

            for (int i = 0; i < RECORDS; i++) {
                writer.writeStartElement("order.entry");
                writer.writeAttribute("id", String.valueOf(i));
                writer.writeEndElement();
            }
            writer.writeEndElement();
            writer.writeEndDocument();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

}

/*
 * Fabric3
 * Copyright (c) 2009-2013 Metaform Systems
 *
 * Fabric3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version, with the
 * following exception:
 *
 * Linking this software statically or dynamically with other
 * modules is making a combined work based on this software.
 * Thus, the terms and conditions of the GNU General Public
 * License cover the whole combination.
 *
 * As a special exception, the copyright holders of this software
 * give you permission to link this software with independent
 * modules to produce an executable, regardless of the license
 * terms of these independent modules, and to copy and distribute
 * the resulting executable under terms of your choice, provided
 * that you also meet, for each linked independent module, the
 * terms and conditions of the license of that module. An
 * independent module is a module which is not derived from or
 * based on this software. If you modify this software, you may
 * extend this exception to your version of the software, but
 * you are not obligated to do so. If you do not wish to do so,
 * delete this exception statement from your version.
 *
 * Fabric3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the
 * GNU General Public License along with Fabric3.
 * If not, see <http://www.gnu.org/licenses/>.
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

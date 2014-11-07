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
package org.fabric3.samples.streaming.server;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.fabric3.api.annotation.monitor.Monitor;
import org.fabric3.api.annotation.scope.Scopes;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
import org.oasisopen.sca.annotation.Scope;

/**
 * Receives a batch file as an input stream, parses it using StAX, and for each entry dispatches to an {@link OrderEntryHandler} based on the QName of the entry
 * element.
 * <p/>
 * The dispatching mechanism uses support for injecting a Map of service references keyed by QName. The QName for each service is configured in the composite
 * for this application using the <code>f3:key</code> attribute.
 */
@Scope(Scopes.COMPOSITE)
public class GatewayServiceImpl implements GatewayService {

    @Monitor
    GatewayMonitor monitor;

    @Reference
    protected Map<QName, EntryHandler> handlers;

    private XMLInputFactory xmlFactory;

    @Init
    public void init() {
        xmlFactory = XMLInputFactory.newFactory();
    }

    public void receive(InputStream stream) {
        try {
            monitor.received();
            parse(stream);
            monitor.completed();
        } catch (XMLStreamException e) {
            monitor.error(e);
        } catch (IOException e) {
            monitor.error(e);
        }
    }

    private void parse(InputStream stream) throws XMLStreamException, IOException {
        XMLStreamReader reader = xmlFactory.createXMLStreamReader(stream);
        reader.nextTag();

        while (true) {
            int val = reader.next();
            switch (val) {
                case XMLStreamConstants.START_ELEMENT:
                    QName name = reader.getName();
                    EntryHandler handler = handlers.get(name);
                    if (handler == null) {
                        throw new IOException("Handler not found for entry type: " + name);
                    }
                    handler.handle(reader);
                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    reader.close();
                    return;
            }
        }
    }
}

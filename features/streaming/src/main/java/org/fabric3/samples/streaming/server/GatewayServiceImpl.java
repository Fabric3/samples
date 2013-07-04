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

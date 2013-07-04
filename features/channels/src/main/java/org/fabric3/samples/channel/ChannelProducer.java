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
package org.fabric3.samples.channel;

import org.fabric3.api.ChannelEvent;
import org.fabric3.api.annotation.Producer;
import org.fabric3.api.annotation.monitor.Monitor;
import org.fabric3.api.annotation.scope.Scopes;
import org.oasisopen.sca.annotation.Scope;

/**
 * Demonstrates the following channel capabilities:
 * <pre>
 *     - Using high-performance ring-buffer channels based on the Disruptor (http://lmax-exchange.github.io/disruptor/)
 *     - Using ordered (sequenced) consumers to first deserializing a message and store its value for subsequent processing in {@link ChannelEvent}
 *     - Worker pools (an event is processed by one consumer)
 *     - Receiving end batch notifications from {@link ChannelEvent#isEndOfBatch()}
 * </pre>
 */
@Scope(Scopes.COMPOSITE)
public class ChannelProducer implements Runnable {
    @Monitor
    protected SystemMonitor monitor;

    private int counter;

    @Producer
    protected WorkChannel workChannel;

    @Producer
    protected TypedChannel typedChannel;

    @Producer
    WorkerPoolChannel workerPoolChannel;

    public void run() {
        monitor.send();

        String message = "This is an event: " + counter++;
        workChannel.send(message.getBytes());  // serialized messages example with ordered processing

        workerPoolChannel.publish(message);    // worker pool example

        Event event = new Event(message);      // typed messages example
        typedChannel.publish(event);

    }
}

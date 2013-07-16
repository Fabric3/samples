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
package org.fabric3.samples.fastquote.aggregation;

import java.util.concurrent.TimeUnit;

import com.google.protobuf.InvalidProtocolBufferException;
import org.fabric3.api.annotation.Consumer;
import org.fabric3.api.annotation.monitor.Monitor;
import org.fabric3.api.annotation.scope.Composite;
import org.fabric3.samples.fastquote.price.Price;
import org.fabric3.samples.fastquote.price.PriceProtos;
import org.fabric3.samples.fastquote.pricing.api.PricingService;
import org.oasisopen.sca.annotation.Reference;

/**
 * Receives base incoming prices from liquidity providers such as a bank or electronic communication network (ECN).
 */
@Composite
public class Gateway {
    private long received;

    @Monitor
    GatewayMonitor monitor;

    @Reference
    protected Journaler journaler;

    @Reference
    protected PricingService pricingService;

    @Consumer("providerChannel")
    public void onPrice(byte[] serialized) {
        try {
            long start = System.nanoTime();
            long correlationId = journaler.record(serialized);

            PriceProtos.Price protoPrice = PriceProtos.Price.parseFrom(serialized);
            Price price = new Price(correlationId);

            pricingService.marginAndSend(price);
            long end = System.nanoTime() - start;
            System.out.println("Processed: " + ++received);
            System.out.println("Latency: " + TimeUnit.MICROSECONDS.convert(end, TimeUnit.NANOSECONDS) + "us");
        } catch (InvalidProtocolBufferException e) {
            monitor.error(e);
        }

    }
}

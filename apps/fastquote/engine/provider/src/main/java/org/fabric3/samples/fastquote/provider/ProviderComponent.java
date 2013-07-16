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
package org.fabric3.samples.fastquote.provider;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.fabric3.api.annotation.Producer;
import org.fabric3.api.annotation.scope.Composite;
import org.fabric3.samples.fastquote.price.PriceProtos;
import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.Property;

/**
 * Simulates a liquidity provider by generating prices that are published to a price channel.
 */
@Composite
public class ProviderComponent implements Runnable {
    private static final int NUMBER_CURRENCY_PAIRS = 4;
    private int sequence = 0;
    private AtomicBoolean shutdown = new AtomicBoolean();
    private Random random = new Random();

    @Property(required = false)
    protected int waitTime = 1;

    @Producer
    protected ProviderChanel providerChannel;

    @Destroy
    public void destroy() {
        shutdown.set(true);
    }

    public void run() {
        while (!shutdown.get()) {
            PriceProtos.Price.Builder builder = PriceProtos.Price.newBuilder();
            builder.setVenueId(0);
            builder.setType(PriceProtos.Price.Type.SPOT).setBidPrice(10).setBidSize(100);
            int modulo = sequence % NUMBER_CURRENCY_PAIRS;
            generatePrice(builder, modulo);
            generateSize(builder, modulo);
            if (sequence == Integer.MAX_VALUE) {
                sequence = 0;
            } else {
                sequence++;
            }
            System.out.println("Sending: " + builder.getBidSize() + "@" + builder.getBidPrice());
            providerChannel.send(builder.build().toByteArray());
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                Thread.interrupted();
            }
        }
    }

    private void generatePrice(PriceProtos.Price.Builder builder, int modulo) {
        double seed = random.nextDouble();
        double price;
        if (modulo == 0) {
            builder.setSymbol("USD/GBP");
            price = .640000000 + (seed * (.640000000 - .660000000));
        } else if (modulo == 1) {
            builder.setSymbol("USD/EUR");
            price = .740000000 + (seed * (.740000000 - .760000000));
        } else if (modulo == 2) {
            builder.setSymbol("EUR/CHF");
            price = 1.24000000 + (seed * (1.24000000 - 1.26000000));
        } else {
            builder.setSymbol("EUR/JPY");
            price = 130.590000 + (seed * (130.590000 - 130.610000));
        }
        builder.setBidPrice(price);
    }

    private void generateSize(PriceProtos.Price.Builder builder, int modulo) {
        if (modulo == 0) {
            builder.setBidSize(100000);
        } else if (modulo == 1) {
            builder.setBidSize(1000000);
        } else if (modulo == 2) {
            builder.setBidSize(10000000);
        } else {
            builder.setBidSize(20000000);
        }
    }

}


/*
 * Fabric3
 * Copyright (c) 2009 Metaform Systems
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
package org.fabric3.samples.eventing;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Scope;

import org.fabric3.api.annotation.Consumer;
import org.fabric3.api.annotation.Resource;

/**
 * Component that subscribes to the buy and sell channels to match sell and buy orders.
 *
 * @version $Rev: 9129 $ $Date: 2010-06-12 23:08:48 +0200 (Sat, 12 Jun 2010) $
 */
@Scope("COMPOSITE")
public class OrderComponent {
    private Queue<BuyOrder> buyOrders = new ConcurrentLinkedQueue<BuyOrder>();

    private ScheduledExecutorService executorService;

    /**
     * Constructor that injects the runtime <code>ScheduledExecutorService</code>, which is used to schedule recurring tasks against the default
     * runtime timer pool.
     *
     * @param executorService the <code>ScheduledExecutorService</code> provided by the runtime
     */
    public OrderComponent(@Resource ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * Initializer. Schedules a timer to check on expired buy orders.
     */
    @Init
    public void init() {
        executorService.scheduleWithFixedDelay(new OrderExpirer(), 120, 120, TimeUnit.SECONDS);
    }

    /**
     * Receives sell orders as they arrive in the sell channel.
     *
     * @param sellOrder a received offer
     */
    @Consumer("sellChannel")
    public void onSell(SellOrder sellOrder) {
        System.out.println("Received a sell order:" + sellOrder.getSymbol() + " @ " + sellOrder.getPrice() + " [" + sellOrder.getId() + "]");
        for (Iterator<BuyOrder> iterator = buyOrders.iterator(); iterator.hasNext();) {
            BuyOrder buyOrder = iterator.next();
            if (match(sellOrder, buyOrder)) {
                System.out.println("Matched orders: " + buyOrder.getSymbol() + " @ " + sellOrder.getPrice() + " [" + sellOrder.getId() + "]");
                iterator.remove();
                break;
            }
        }
    }

    /**
     * Receives buy orders as they arrive in the order channel.
     *
     * @param buyOrder a received buy request
     */
    @Consumer("buyChannel")
    public void onBuy(BuyOrder buyOrder) {
        System.out.println("Received an buy order:" + buyOrder.getSymbol() + " @ " + buyOrder.getMaxPrice() + " [" + buyOrder.getId() + "]");
        buyOrders.add(buyOrder);
    }

    private boolean match(SellOrder sellOrder, BuyOrder buyOrder) {
        return (sellOrder.getSymbol().equals(buyOrder.getSymbol()) && sellOrder.getPrice() <= buyOrder.getMaxPrice());
    }

    private class OrderExpirer implements Runnable {

        public void run() {
            long now = System.currentTimeMillis();
            for (Iterator<BuyOrder> iterator = buyOrders.iterator(); iterator.hasNext();) {
                BuyOrder buyOrder = iterator.next();
                if (buyOrder.getExpireTime() <= now) {
                    System.out.println("Expired buy order: " + buyOrder.getSymbol() + " @ " + buyOrder.getMaxPrice() + " [" + buyOrder.getId() + "]");
                    iterator.remove();
                }
            }
        }
    }

}

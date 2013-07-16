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
package org.fabric3.samples.fastquote.pricing.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.fabric3.api.annotation.scope.Composite;
import org.fabric3.samples.fastquote.price.PriceLadder;
import org.fabric3.samples.fastquote.price.PriceRung;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;

/**
 *
 */
@Composite
@EagerInit
public class PriceLadderCacheImpl implements PriceLadderCache {
    private AtomicReference<Map<String, PriceLadderPair>> ladders = new AtomicReference<Map<String, PriceLadderPair>>();

    @Init
    public void init() {
        // initialize data
        PriceRung[] rungs = new PriceRung[101];
        int step = 5000000;
        int increment = 50000;
        for (int i = 0, n = 0; i < step; i = i + increment, n++) {
            PriceRung rung = new PriceRung(i + 1, i + increment, 0.001);
            rungs[n] = rung;
        }
        rungs[100] = new PriceRung(step + 1, Double.MAX_VALUE, 2.00);

        PriceLadder bidLadder = new PriceLadder(rungs);
        PriceLadder askLadder = new PriceLadder(rungs);
        PriceLadderPair pair = new PriceLadderPair(bidLadder, askLadder);

        Map<String, PriceLadderPair> pairs = new HashMap<String, PriceLadderPair>();

        pairs.put("USD/GBP", pair);
        pairs.put("USD/EUR", pair);
        pairs.put("EUR/CHF", pair);
        pairs.put("EUR/JPY", pair);

        // update atomically
        ladders.set(pairs);
    }

    public PriceLadderPair getLadders(String symbol) {
        return ladders.get().get(symbol);
    }
}

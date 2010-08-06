package org.fabric3.samples.eventing;

import java.io.Serializable;

/**
 * A buy order.
 *
 * @version $Rev$ $Date$
 */
public class BuyOrder implements Serializable {
    private static final long serialVersionUID = 1889173891755437967L;

    private long id;
    private String symbol;
    private double maxPrice;
    private long expireTime;

    public BuyOrder(long id, String symbol, double maxPrice, long expireTime) {
        this.id = id;
        this.symbol = symbol;
        this.maxPrice = maxPrice;
        this.expireTime = expireTime;
    }

    public long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public long getExpireTime() {
        return expireTime;
    }
}

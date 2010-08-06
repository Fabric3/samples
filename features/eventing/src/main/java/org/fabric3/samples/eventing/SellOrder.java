package org.fabric3.samples.eventing;

import java.io.Serializable;

/**
 * A sell order.
 *
 * @version $Rev$ $Date$
 */
public class SellOrder implements Serializable {
    private static final long serialVersionUID = 3752564464816023570L;
    private long id;
    private String symbol;
    private double price;

    public SellOrder(long id, String symbol, double price) {
        this.id = id;
        this.symbol = symbol;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

}

package org.fabric3.samples.eventing;

/**
 * Used to calculate when a timer will fire next. This implementation fires a timer using a random wait period of 3 to 5 seconds.
 *
 * @version $Rev$ $Date$
 */
public class TimerInterval {

    public long nextInterval() {
        return 3000 + (int) (Math.random() * 5000);
    }
}

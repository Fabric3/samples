package org.fabric3.samples.timer;

/**
 * Used to calculate when a timer will fire next. This implementation fires a timer every ten seconds.
 *
 * @version $Rev$ $Date$
 */
public class TimerInterval {

    public long nextInterval() {
        return 10000;
    }
}

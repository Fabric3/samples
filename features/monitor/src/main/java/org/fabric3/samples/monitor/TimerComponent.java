package org.fabric3.samples.monitor;

import java.util.concurrent.atomic.AtomicInteger;

import org.oasisopen.sca.annotation.Scope;

import org.fabric3.api.annotation.monitor.Monitor;

/**
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
public class TimerComponent implements Runnable {
    private TimerMonitor monitor;
    private AtomicInteger count;

    public TimerComponent(@Monitor("MonitorApplicationChannel") TimerMonitor monitor) {
        this.monitor = monitor;
        count = new AtomicInteger(0);
    }

    public void run() {
        if (count.getAndIncrement() == 5) {
            monitor.error(new ErrorEvent("An error was raised"));
            count.set(0);
        } else {
            monitor.message("This is an event");
        }
    }
}

package org.fabric3.samples.monitor;

import java.util.concurrent.atomic.AtomicInteger;

import org.fabric3.api.annotation.monitor.Monitor;
import org.oasisopen.sca.annotation.Scope;

/**
 *
 */
@Scope("COMPOSITE")
public class TimerComponent implements Runnable {
    private TimerMonitor monitor;
    private AtomicInteger count;

    public TimerComponent(@Monitor("ApplicationDestination") TimerMonitor monitor) {
        this.monitor = monitor;
        count = new AtomicInteger(0);
    }

    public void run() {
        if (count.getAndIncrement() == 5) {
            monitor.error(new Exception("An error was raised"));
            count.set(0);
        } else {
            monitor.message("This is a message");
        }
    }
}

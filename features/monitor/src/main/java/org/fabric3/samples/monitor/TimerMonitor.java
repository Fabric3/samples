package org.fabric3.samples.monitor;

import org.fabric3.api.annotation.monitor.Info;
import org.fabric3.api.annotation.monitor.Severe;

/**
 * Dispatches messages to a monitor destination.
 */
public interface TimerMonitor {

    @Severe("The following error was encountered")
    void error(Throwable error);

    @Info("A message was sent: {0}")
    void message(String message);

}

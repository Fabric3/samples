package org.fabric3.samples.monitor;

import org.fabric3.api.annotation.monitor.Info;
import org.fabric3.api.annotation.monitor.Severe;

/**
 * @version $Rev$ $Date$
 */
public interface TimerMonitor {

    @Severe
    void error(ErrorEvent error);

    @Info("A message was sent: {0}")
    void message(String message);


}

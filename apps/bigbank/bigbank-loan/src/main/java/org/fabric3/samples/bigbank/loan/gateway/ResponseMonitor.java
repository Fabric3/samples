package org.fabric3.samples.bigbank.loan.gateway;

import org.fabric3.api.annotation.monitor.Severe;

/**
 * @version $Rev$ $Date$
 */
public interface ResponseMonitor {

    @Severe
    void error(String message, Throwable t);
}

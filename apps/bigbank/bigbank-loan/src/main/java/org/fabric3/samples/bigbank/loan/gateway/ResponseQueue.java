package org.fabric3.samples.bigbank.loan.gateway;

import java.io.OutputStream;

import org.oasisopen.sca.ServiceRuntimeException;

/**
 * @version $Rev$ $Date$
 */
public interface ResponseQueue {

    /**
     * Returns a stream that can be used to write to the response queue.
     *
     * @param key the data key
     * @return the stream
     * @throws ServiceRuntimeException if an error occurs writing the data
     */
    OutputStream openStream(String key) throws ServiceRuntimeException;

}

package org.fabric3.samples.monitor;

import java.io.Serializable;

/**
 * @version $Rev$ $Date$
 */
public class ErrorEvent implements Serializable {
    private static final long serialVersionUID = 8987250338693811241L;
    private String message;

    public ErrorEvent(String message) {
        this.message = message;
    }

    public ErrorEvent() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

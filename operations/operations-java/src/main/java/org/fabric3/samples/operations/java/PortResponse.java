package org.fabric3.samples.operations.java;

import java.util.List;
import java.util.Map;

/**
 * Binds a port query response.
 *
 * @version $Rev$ $Date$
 */
public class PortResponse {
    Map<String, List<Pair>> value;
    Object selfLink;

    public Map<String, List<Pair>> getValue() {
        return value;
    }

    public void setValue(Map<String, List<Pair>> value) {
        this.value = value;
    }

    public Object getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(Object selfLink) {
        this.selfLink = selfLink;
    }
}

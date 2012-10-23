package org.fabric3.samples.bigbank.api.message;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @version $Rev$ $Date$
 */
@XmlRootElement
public class LoanApplicationStatus implements Serializable {
    private static final long serialVersionUID = 3464383487403179368L;

    public static final String SUBMITTED = "SUBMITTED";
    public static final String RATED = "RATED";
    public static final String FINAL = "FINAL";
    public static final String INVALID = "INVALID";

    private String trackingNumber;
    private String status;

    public LoanApplicationStatus() {
    }

    public LoanApplicationStatus(String trackingNumber, String status) {
        this.trackingNumber = trackingNumber;
        this.status = status;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

package org.fabric3.samples.bigbank.api.rest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @version $Rev$ $Date$
 */
@XmlRootElement
public class LoanApplicationSubmission {
    private String trackingNumber;

    public LoanApplicationSubmission() {
    }

    public LoanApplicationSubmission(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
}

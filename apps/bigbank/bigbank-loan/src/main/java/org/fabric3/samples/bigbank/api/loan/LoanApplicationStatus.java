package org.fabric3.samples.bigbank.api.loan;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @version $Rev$ $Date$
 */
@XmlRootElement(namespace = "http://loan.api.bigbank.samples.fabric3.org/")
public class LoanApplicationStatus implements Serializable {
    private static final long serialVersionUID = 3464383487403179368L;

    public static final String SUBMITTED = "SUBMITTED";
    public static final String RATED = "RATED";
    public static final String FINAL = "FINAL";
    public static final String INVALID = "INVALID";

    private String clientCorrelation;
    private String status;

    public LoanApplicationStatus() {
    }

    public LoanApplicationStatus(String clientCorrelation, String status) {
        this.clientCorrelation = clientCorrelation;
        this.status = status;
    }

    public String getClientCorrelation() {
        return clientCorrelation;
    }

    public String getStatus() {
        return status;
    }

    public void setClientCorrelation(String clientCorrelation) {
        this.clientCorrelation = clientCorrelation;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

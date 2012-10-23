package org.fabric3.samples.bigbank.client;

import java.net.URL;
import javax.xml.namespace.QName;

import org.fabric3.samples.bigbank.api.loan.LoanApplication;
import org.fabric3.samples.bigbank.api.loan.LoanApplicationStatus;
import org.fabric3.samples.bigbank.api.loan.LoanService;
import org.fabric3.samples.bigbank.api.loan.LoanServiceService;

/**
 * Invokes the loan service using Web Services (WS-*).
 *
 * @version $Rev$ $Date$
 */
public class LoanClient {

    private static final QName SERVICE_NAME = new QName("http://loan.api.bigbank.samples.fabric3.org/", "LoanServiceService");

    public static void main(String args[]) throws Exception {
        URL wsdlLocation = new URL("http://localhost:8181/wsloan?wsdl");

        LoanServiceService service = new LoanServiceService(wsdlLocation, SERVICE_NAME);
        LoanService loanService = service.getLoanServicePort();

        // create the application
        LoanApplication application = new LoanApplication();
        application.setAmount(100000);
        application.setEin("111111");
        // application.setEin("111123");  // id that will fail credit check

        // invoke the service
        String trackingNumber = loanService.apply(application);

        // display the tracking number
        System.out.println("Submitted loan and received tracking number: " + trackingNumber);

        // check the status
        LoanApplicationStatus status = loanService.getStatus(trackingNumber);
        System.out.println("Loan status: " + status.getStatus());
    }
}

package org.fabric3.samples.bigbank.client;

import javax.xml.namespace.QName;
import java.net.URL;
import java.util.UUID;

import org.fabric3.samples.bigbank.api.loan.LoanApplication;
import org.fabric3.samples.bigbank.api.loan.LoanApplicationStatus;
import org.fabric3.samples.bigbank.api.loan.LoanService;
import org.fabric3.samples.bigbank.api.loan.LoanServiceService;

/**
 * Invokes the loan service using Web Services (WS-*).
 * <p/>
 * Note in a clustered environment, set wsdlLocation URL to the correct zone1 HTTP port.
 */
public class WebServiceLoanClient {
    private static final QName SERVICE_NAME = new QName("http://loan.api.bigbank.samples.fabric3.org/", "LoanServiceService");

    public static void main(String args[]) throws Exception {
        // Note: When using clustered deployment, the port must be set to the zone1 runtime, which is dynamically allocated at startup
        // Please see the monitor logs for the HTTP port
        URL wsdlLocation = new URL("http://localhost:8181/wsloan?wsdl");

        LoanServiceService service = new LoanServiceService(wsdlLocation, SERVICE_NAME);
        LoanService loanService = service.getLoanServicePort();

        // create the application
        LoanApplication application = new LoanApplication();
        application.setAmount(100000);
        application.setEin("111111");
        // application.setEin("111123");  // id that will fail credit check
        String correlationId = UUID.randomUUID().toString();
        application.setClientCorrelation(correlationId);
        application.setNotificationAddress("none");

        // invoke the service
        loanService.apply(application);

        // display the tracking number
        System.out.println("Submitted loan: " + correlationId);

        // check the status
        LoanApplicationStatus status = loanService.getStatus(correlationId);
        System.out.println("Loan status: " + status.getStatus());
    }
}

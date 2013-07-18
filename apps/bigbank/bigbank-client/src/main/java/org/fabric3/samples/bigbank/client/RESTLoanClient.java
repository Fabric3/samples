package org.fabric3.samples.bigbank.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fabric3.samples.bigbank.api.loan.LoanApplication;
import org.fabric3.samples.bigbank.api.loan.LoanApplicationStatus;

/**
 * Invokes the loan service using HTTP.
 * <p/>
 * Note in a clustered environment, set {@link #BASE_URL} to the correct zone1 HTTP port.
 */
public class RESTLoanClient {
    // Note: When using clustered deployment, the port must be set to the zone1 runtime, which is dynamically allocated at startup
    // Please see the monitor logs for the HTTP port

    private static final String BASE_URL = "http://localhost:8182/rsloan/application/";

    private ObjectMapper mapper = new ObjectMapper();

    public static void main(String args[]) throws Exception {
        RESTLoanClient client = new RESTLoanClient();
        client.submitApplication();
    }

    public void submitApplication() throws Exception {
        URL applicationUrl = new URL(BASE_URL);
        LoanApplication application = new LoanApplication();
        application.setAmount(1);
        application.setAmount(100000);
        application.setEin("111111");
        // application.setEin("111123");  // id that will fail credit check
        String correlationId = UUID.randomUUID().toString();
        application.setClientCorrelation(correlationId);
        application.setNotificationAddress("none");

        HttpURLConnection connection = createConnection(applicationUrl, "POST");
        connection.connect();
        OutputStream stream = connection.getOutputStream();
        mapper.writeValue(stream, application);

        int code = connection.getResponseCode();
        if (code != 200 && code != 204) {
            System.out.println("Error submitting loan application: " + code);
            return;
        }
        connection.disconnect();

        // display the tracking number
        System.out.println("Submitted loan: " + correlationId);

        // check the status
        URL statusUrl = new URL(BASE_URL + correlationId);
        connection = createConnection(statusUrl, "GET");
        connection.connect();

        LoanApplicationStatus status = mapper.readValue(connection.getInputStream(), LoanApplicationStatus.class);

        connection.disconnect();

        System.out.println("Loan status: " + status.getStatus());
    }

    private HttpURLConnection createConnection(URL url, String verb) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod(verb);
        connection.setRequestProperty("Content-type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        return connection;
    }

}

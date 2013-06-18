package org.fabric3.samples.bigbank.client;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;

import org.fabric3.samples.bigbank.api.loan.LoanApplication;
import org.fabric3.samples.bigbank.api.loan.LoanApplicationStatus;
import org.fabric3.samples.bigbank.api.loan.ObjectFactory;

/**
 * Demonstrates connecting to the loan service using an asynchronous file exchange. A loan application is placed in an inbox directory on disk and the client
 * monitors an outbox directory for a response.
 */
public class FileSystemClient {
    private JAXBContext context;

    public FileSystemClient() throws JAXBException {
        context = JAXBContext.newInstance("org.fabric3.samples.bigbank.api.loan");
    }

    public static void main(String[] args) throws Exception {
        new FileSystemClient().submitApplication();
    }

    @SuppressWarnings({"unchecked"})
    public void submitApplication() throws Exception {

        // ********** You must update the runtime location to the absolute path of the receive queue is on the server, e.g.:
        //             <runtime dir>/image/runtimes/<server>/data/inbox/receive.queue   **********

        String RUNTIME_LOCATION = "";
        File inboxLocation = new File(
                RUNTIME_LOCATION + File.separator + "data" + File.separator + "inbox" + File.separator + "receive.queue" + File.separator);
        File outboxLocation = new File(
                RUNTIME_LOCATION + File.separator + "data" + File.separator + "outbox" + File.separator + "response.queue" + File.separator);

        if (!inboxLocation.exists()) {
            throw new AssertionError("The location of the inbox directory is not set properly. It must be an absolute directory pointing to "
                                     + "image/runtimes/<server>/data/inbox/receive.queue");
        }
        if (!outboxLocation.exists()) {
            throw new AssertionError("The location of the outbox directory is not set properly. It must be an absolute directory pointing to "
                                     + "image/runtimes/<server>/data/inbox/receive.queue");
        }

        LoanApplication application = new LoanApplication();
        application.setAmount(1);
        application.setAmount(100000);
        application.setEin("111111");
        // application.setEin("111123");  // id that will fail credit check
        String correlationId = UUID.randomUUID().toString();
        application.setClientCorrelation(correlationId);
        application.setNotificationAddress("file");

        String fileName = "message-" + System.nanoTime() + ".xml";  // this is not unique but good enough for a demo
        FileOutputStream stream = new FileOutputStream(new File(inboxLocation, fileName));
        JAXBElement<LoanApplication> element = new ObjectFactory().createLoanApplication(application);
        context.createMarshaller().marshal(element, stream);

        File outputFile = new File(outboxLocation, correlationId + ".xml");
        System.out.print("Waiting for response file.");
        long time = 0;
        while (!outputFile.exists()) {
            System.out.print(".");
            if (time > 10000) {
                System.out.println("Error - No response file returned before timeout");
                return;
            }
            time = time + 1000;
            Thread.sleep(1000);
        }
        Unmarshaller unmarshaller = context.createUnmarshaller();
        FileInputStream responseStream = new FileInputStream(outputFile);
        JAXBElement<LoanApplicationStatus> statusElement = (JAXBElement<LoanApplicationStatus>) unmarshaller.unmarshal(responseStream);
        System.out.println("\nReceived a response, loan status is: " + statusElement.getValue().getStatus());
        outputFile.delete();
    }
}

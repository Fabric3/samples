package org.fabric3.samples.operations.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Demonstrates a Fabric3 Resource Management Framework (RMF) client.
 * <p/>
 * The Resource Management Framework is a hypermedia API for managing and administering a Fabric3 domain that follows REST principles. The client
 * performs the following operations:
 * <p/>
 * <p/>
 * - Lists deployed components
 * <p/>
 * - Queries the active zones in the domain
 * <p/>
 * - Follows links to each zone and queries the threadpool configuration
 * <p/>
 * - Updates the threadpool core size for each zone to demonstrate cluster replication
 * <p/>
 * - Queries the active runtimes in the domain
 * <p/>
 * - Follows links to each runtime and queries the allocated ports
 * <p/>
 * </pre>
 * <p/>
 * RMF supports JSON for data serialization.
 * <p/>
 * <strong>Note that the client is configured to use the default single-VM runtime HTTP port (8181). When using the client with the samples
 * distributed domain, the {@link #BASE_URL} must be changed to {@link #DISTRIBUTED_URL}.
 *
 * @version $Rev$ $Date$
 */
public class OperationsClient {
    private static final String VM_URL = "http://localhost:8181/management/";
    private static final String DISTRIBUTED_URL = "http://localhost:8180/management/";

    private static final String BASE_URL = VM_URL;

    private OperationsHelper helper = new OperationsHelper();

    public static void main(String[] args) throws Exception {
        OperationsClient client = new OperationsClient();

        client.getComponents();

        Map<String, String> zones = client.getZones();

        for (Map.Entry<String, String> entry : zones.entrySet()) {
            client.navigateZone(entry.getKey(), entry.getValue());
        }

        Map<String, String> runtimes = client.getRuntimes();
        for (Map.Entry<String, String> entry : runtimes.entrySet()) {
            client.navigateRuntime(entry.getKey(), entry.getValue());
        }
    }

    private void getComponents() throws IOException {
        HttpURLConnection connection = helper.createConnection(BASE_URL + "domain/components", "", "GET");
        connection.connect();
        int code = connection.getResponseCode();
        if (code != 200) {
            System.out.println("Error. HTTP response: " + code);
            return;
        }
        InputStream stream = connection.getInputStream();
        Map<String, List<Map<String, String>>> response = helper.parse(Object.class, stream);

        if (response.get("components") == null) {
            // single component returned
            System.out.println("   " + response.get("uri") + " [Zone: " + response.get("zone") + "]");
        } else {
            List<Map<String, String>> components = response.get("components");
            if (components.isEmpty()) {
                System.out.println("No deployed components");
            } else {
                System.out.println("Deployed Components:\n");
                for (Map<String, String> component : components) {
                    System.out.println("   " + component.get("uri") + " [" + component.get("zone") + "]");
                }
            }
        }
        System.out.println("\n");
    }

    private Map<String, String> getZones() throws IOException {
        Map<String, String> links = new HashMap<String, String>();

        HttpURLConnection connection = helper.createConnection(BASE_URL + "domain/zones", "", "GET");
        connection.connect();
        int code = connection.getResponseCode();
        if (code != 200) {
            System.out.println("Error. HTTP response: " + code);
            return links;
        }
        InputStream stream = connection.getInputStream();
        Map<String, List<Map<String, String>>> response = helper.parse(Object.class, stream);

        List<Map<String, String>> zones = response.get("value");
        if (zones.isEmpty()) {
            System.out.println("No zones");
        } else {
            System.out.println("Zones:\n");
            for (Map<String, String> zone : zones) {
                String name = zone.get("name");
                String link = zone.get("href");
                links.put(name, link);
                System.out.println("   " + name + " [" + link + "]");
            }
        }
        System.out.println("\n");
        return links;
    }

    private void navigateZone(String name, String address) throws IOException {
        System.out.println("Navigating to zone: " + name + "\n");

        getThreadPoolStatistics(address);
        updatePoolSize(address);

        getTransports(address);
        System.out.println("\n");
    }

    private void getThreadPoolStatistics(String address) throws IOException {
        HttpURLConnection connection = helper.createConnection(address + "/runtime/threadpool", "", "GET");
        connection.connect();
        int code = connection.getResponseCode();
        if (code != 200) {
            System.out.println("Error. HTTP response: " + code);
            return;
        }
        InputStream stream = connection.getInputStream();
        Map<String, List<Map<String, String>>> response = helper.parse(Object.class, stream);
        System.out.println("   Thread pool statistics");
        System.out.println("      Completed work: " + response.get("completedworkcount"));
        System.out.println("      Core size: " + response.get("corepoolsize"));
        System.out.println("      Active count: " + response.get("activecount"));
        System.out.println("\n");
    }

    private void updatePoolSize(String address) throws IOException {
        HttpURLConnection connection = helper.createConnection(address + "/runtime/threadpool/corepoolsize", "", "POST");
        connection.connect();
        OutputStream stream = connection.getOutputStream();
        helper.serialize("30", stream);
        int code = connection.getResponseCode();
        if (code != 200) {
            System.out.println("Error. HTTP response: " + code);
            return;
        }
        System.out.println("   Core pool size updated and propagated in zone\n");
    }

    private void getTransports(String address) throws IOException {
        HttpURLConnection connection = helper.createConnection(address + "/runtime/transports", "", "GET");
        connection.connect();
        int code = connection.getResponseCode();
        if (code != 200) {
            System.out.println("Error. HTTP response: " + code);
            return;
        }
        InputStream stream = connection.getInputStream();
        Map<String, List<String>> response = helper.parse(Object.class, stream);
        List<String> transports = response.get("value");
        StringBuilder builder = new StringBuilder("   Transports: ");
        for (String transport : transports) {
            builder.append(transport).append(" ");
        }
        System.out.println(builder + "\n");
    }

    private Map<String, String> getRuntimes() throws IOException {
        Map<String, String> links = new HashMap<String, String>();

        HttpURLConnection connection = helper.createConnection(BASE_URL + "domain/runtimes", "", "GET");
        connection.connect();
        int code = connection.getResponseCode();
        if (code != 200) {
            System.out.println("Error. HTTP response: " + code);
            return links;
        }
        InputStream stream = connection.getInputStream();
        Map<String, List<Map<String, String>>> response = helper.parse(Object.class, stream);

        List<Map<String, String>> runtimes = response.get("value");
        if (runtimes.isEmpty()) {
            System.out.println("No runtimes");
        } else {
            System.out.println("Runtimes:\n");
            for (Map<String, String> runtime : runtimes) {
                String name = runtime.get("name");
                String link = runtime.get("href");
                links.put(name, link);
                System.out.println("   " + name + " [" + link + "]");
            }
        }
        System.out.println("\n");
        return links;
    }

    private void navigateRuntime(String name, String address) throws IOException {
        System.out.println("Navigating to runtime: " + name + "\n");

        getPorts(address);
        System.out.println("\n");
    }

    private void getPorts(String address) throws IOException {
        HttpURLConnection connection = helper.createConnection(address + "/ports", "", "GET");
        connection.connect();
        int code = connection.getResponseCode();
        if (code != 200) {
            System.out.println("Error. HTTP response: " + code);
            return;
        }
        InputStream stream = connection.getInputStream();
        PortResponse response = helper.parse(PortResponse.class, stream);
        System.out.println("   Runtime Ports");
        for (Map.Entry<String, List<Pair>> entry : response.getValue().entrySet()) {
            System.out.println("      " + entry.getKey());
            for (Pair pair : entry.getValue()) {
                System.out.println("         Name: " + pair.getName() + " Port: " + pair.getPort());

            }

        }
        System.out.println("\n");
    }


}

package org.fabric3.samples.operations.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 */
public class OperationsHelper {
    private ObjectMapper mapper;

    public OperationsHelper() {
        mapper = new ObjectMapper();
    }

    public HttpURLConnection createConnection(String address, String path, String verb) throws IOException {
        URL url = createUrl(address + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod(verb);
        connection.setRequestProperty("Content-type", "application/json");
        return connection;
    }

    @SuppressWarnings({"unchecked"})
    public <T> T parse(Class<?> type, InputStream stream) throws IOException {
        JsonParser jp = mapper.getFactory().createParser(stream);
        return (T) mapper.readValue(jp, type);
    }

    public void serialize(String message, OutputStream stream) throws IOException {
        byte[] bytes = mapper.writeValueAsBytes(message);
        stream.write(bytes);
    }

    private URL createUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }

}

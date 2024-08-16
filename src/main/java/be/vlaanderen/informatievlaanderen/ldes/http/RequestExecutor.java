package be.vlaanderen.informatievlaanderen.ldes.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;

@Component
public class RequestExecutor {
    private static final List<Integer> ACCEPTABLE_STATUS_CODES = List.of(200, 201);
    private final HttpClient httpClient;

    public RequestExecutor(HttpClient httpClient) {
	    this.httpClient = httpClient;
    }

    public HttpEntity execute(Request request) {
        return execute(request, ACCEPTABLE_STATUS_CODES);
    }

    public HttpEntity execute(Request request, Integer... expectedStatusCodes) {
        return execute(request, Arrays.asList(expectedStatusCodes));
    }

    public HttpEntity execute(Request request, List<Integer> expectedCodes) {
        try {
            HttpResponse response = httpClient.execute(request.createRequest());
            if (!expectedCodes.contains(response.getStatusLine().getStatusCode())) {
                String msg = new BufferedReader(new InputStreamReader(response.getEntity().getContent())).readLine();
                throw new RuntimeException(msg);
            }

            return response.getEntity();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

package be.vlaanderen.informatievlaanderen.ldes.http;

import be.vlaanderen.informatievlaanderen.ldes.http.requests.HttpRequest;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
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

    public HttpEntity execute(HttpRequest request) {
        return execute(request, ACCEPTABLE_STATUS_CODES);
    }

    public HttpEntity execute(HttpRequest request, Integer... expectedStatusCodes) {
        return execute(request, Arrays.asList(expectedStatusCodes));
    }

    public HttpEntity execute(HttpRequest request, List<Integer> expectedCodes) {
        try {
            HttpResponse response = httpClient.execute(request.createRequest());
            if (!expectedCodes.contains(response.getStatusLine().getStatusCode())) {
                final String message = EntityUtils.toString(response.getEntity());
                throw new IllegalStateException("Unexpected response status: " + response.getStatusLine().getStatusCode() + ":\n" + message);
            }

            return response.getEntity();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

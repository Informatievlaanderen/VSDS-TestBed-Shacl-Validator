package be.vlaanderen.informatievlaanderen.ldes.http;

import be.vlaanderen.informatievlaanderen.ldes.http.requests.HttpRequest;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;

@Component
public class RequestExecutor {
	private static final List<Integer> ACCEPTABLE_STATUS_CODES = List.of(200, 201);
	private static final Logger log = LoggerFactory.getLogger(RequestExecutor.class);
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
			log.atInfo().log("Starting to execute request: {}", request.getUrl());
			HttpResponse response = httpClient.execute(request.createRequest());
			log.atInfo().log("Received response status: {}", response.getStatusLine().getStatusCode());
			if (!expectedCodes.contains(response.getStatusLine().getStatusCode())) {
				final String message = EntityUtils.toString(response.getEntity());
				log.atWarn().log("Unexpected response status: {}\n{}", response.getStatusLine().getStatusCode(), message);
				throw new IllegalStateException("Unexpected response status: " + response.getStatusLine().getStatusCode() + ":\n" + message);
			}

			return response.getEntity();
		} catch (IOException e) {
			log.atError().log("IOError received: {}", e.getMessage());
			throw new UncheckedIOException(e);
		} catch (RuntimeException e) {
			log.atError().log("RuntimeError received: {}", e.getMessage());
			throw e;
		}
	}
}

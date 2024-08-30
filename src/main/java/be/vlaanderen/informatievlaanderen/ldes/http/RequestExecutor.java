package be.vlaanderen.informatievlaanderen.ldes.http;

import be.vlaanderen.informatievlaanderen.ldes.http.requests.HttpRequest;
import org.apache.http.client.HttpClient;
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

	public HttpResponse execute(HttpRequest request) {
		return execute(request, ACCEPTABLE_STATUS_CODES);
	}

	public HttpResponse execute(HttpRequest request, Integer... expectedStatusCodes) {
		return execute(request, Arrays.asList(expectedStatusCodes));
	}

	public HttpResponse execute(HttpRequest request, List<Integer> expectedCodes) {
		try {
			log.atDebug().log("Starting to execute request: {}", request.getUrl());
			final var response = HttpResponse.from(httpClient.execute(request.createRequest()));
			log.atDebug().log("Received response status: {}", response.getStatusCode());
			checkResponseForStatusCodes(response, expectedCodes);
			return response;
		} catch (IOException e) {
			log.atError().log("IOError received: {}", e.getMessage());
			throw new UncheckedIOException(e);
		} catch (RuntimeException e) {
			log.atError().log("RuntimeError received: {}", e.getMessage());
			throw e;
		}
	}

	private static void checkResponseForStatusCodes(HttpResponse response, List<Integer> expectedCodes) {
		if (!expectedCodes.contains(response.getStatusCode())) {
			final String message = response.getBody().orElse("NO MESSAGE PROVIDED");
			log.atWarn().log("Unexpected response status: {}\n{}", response.getStatusCode(), message);
			throw new IllegalStateException("Unexpected response status: " + response.getStatusCode() + ":\n" + message);
		}
	}
}

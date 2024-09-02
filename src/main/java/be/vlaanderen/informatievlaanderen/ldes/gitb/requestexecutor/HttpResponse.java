package be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor;

import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public final class HttpResponse {
	private final int statusCode;
	private final String body;

	public HttpResponse(int statusCode, String body) {
		this.statusCode = statusCode;
		this.body = body;
	}

	public static HttpResponse from(org.apache.http.HttpResponse response) throws IOException {
		return new HttpResponse(
				response.getStatusLine().getStatusCode(),
				response.getEntity() != null ? EntityUtils.toString(response.getEntity()) : null
		);
	}

	public int getStatusCode() {
		return statusCode;
	}

	public Optional<String> getBody() {
		return Optional.ofNullable(body).filter(content -> !content.isBlank());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (HttpResponse) obj;
		return this.statusCode == that.statusCode &&
				Objects.equals(this.body, that.body);
	}

	@Override
	public int hashCode() {
		return Objects.hash(statusCode, body);
	}

	@Override
	public String toString() {
		return "HttpResponse[" +
				"statusCode=" + statusCode + ", " +
				"body=" + body + ']';
	}

}

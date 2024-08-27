package be.vlaanderen.informatievlaanderen.ldes.http.requests;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import java.util.Objects;

public class GetRequest implements HttpRequest {
	private final String url;

	public GetRequest(String url) {
		this.url = url;
	}

	@Override
	public HttpRequestBase createRequest() {
		return new HttpGet(url);
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GetRequest that)) return false;

		return Objects.equals(url, that.url);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(url);
	}
}

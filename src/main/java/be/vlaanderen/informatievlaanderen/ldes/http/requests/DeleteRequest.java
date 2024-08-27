package be.vlaanderen.informatievlaanderen.ldes.http.requests;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpRequestBase;

import java.util.Objects;

public class DeleteRequest implements HttpRequest {
	private final String url;

	public DeleteRequest(String url) {
		this.url = url;
	}

	@Override
	public HttpRequestBase createRequest() {
		return new HttpDelete(url);
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof DeleteRequest that)) return false;

		return Objects.equals(url, that.url);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(url);
	}
}

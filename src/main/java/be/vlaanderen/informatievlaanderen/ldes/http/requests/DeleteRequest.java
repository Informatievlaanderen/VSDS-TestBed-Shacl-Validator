package be.vlaanderen.informatievlaanderen.ldes.http.requests;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpRequestBase;

public class DeleteRequest implements HttpRequest {
	private final String url;

	public DeleteRequest(String url) {
		this.url = url;
	}

	@Override
	public HttpRequestBase createRequest() {
		return new HttpDelete(url);
	}
}

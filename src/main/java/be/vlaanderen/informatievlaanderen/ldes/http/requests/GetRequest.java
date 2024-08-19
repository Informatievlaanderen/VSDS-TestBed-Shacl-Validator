package be.vlaanderen.informatievlaanderen.ldes.http.requests;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

public class GetRequest implements HttpRequest {
	private final String url;

	public GetRequest(String url) {
		this.url = url;
	}

	@Override
	public HttpRequestBase createRequest() {
		return new HttpGet(url);
	}
}

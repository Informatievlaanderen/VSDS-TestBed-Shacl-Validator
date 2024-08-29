package be.vlaanderen.informatievlaanderen.ldes.http.requests;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

public class PostRequest implements HttpRequest {
	private final String url;
	private final String body;
	private final ContentType contentType;

	public PostRequest(String url, String body, ContentType contentType) {
		this.url = url;
		this.body = body;
		this.contentType = contentType;
	}

	public PostRequest(String url, String body, String contentType) {
		this(url, body, ContentType.parse(contentType));
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public HttpRequestBase createRequest() {
		final var request = new HttpPost(url);
		request.setEntity(new StringEntity(body, contentType));
		return request;
	}
}

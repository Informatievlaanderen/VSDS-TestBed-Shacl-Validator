package be.vlaanderen.informatievlaanderen.ldes.http;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.UnsupportedEncodingException;

public class Request {
    private final String url;
    private final String body;
    private final RequestMethod method;
    private final String contentType;

    public Request(String url, String body, RequestMethod method, ContentType contentType) {
        this.url = url;
        this.body = body;
        this.method = method;
        this.contentType = contentType.getMimeType();
    }

    public Request(String url, String body, RequestMethod method, String contentType) {
        this.url = url;
        this.body = body;
        this.method = method;
        this.contentType = contentType;
    }

    public Request(String url, RequestMethod method) {
        this(url, "", method, ContentType.DEFAULT_TEXT);
    }

    public HttpRequestBase createRequest() throws UnsupportedEncodingException {
        return switch (method) {
            case GET -> new HttpGet(url);
            case POST -> {
                final HttpPost post = new HttpPost(url);
                post.setEntity(new StringEntity(body, ContentType.parse(contentType)));
                yield post;
            }
            case DELETE -> new HttpDelete(url);
            default -> throw new IllegalStateException("Http method not supported: " + method);
        };
    }
}

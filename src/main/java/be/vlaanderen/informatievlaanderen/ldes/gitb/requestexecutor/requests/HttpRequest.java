package be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.requests;

import org.apache.http.client.methods.HttpRequestBase;

public interface HttpRequest {
	String getUrl();
	HttpRequestBase createRequest();
}

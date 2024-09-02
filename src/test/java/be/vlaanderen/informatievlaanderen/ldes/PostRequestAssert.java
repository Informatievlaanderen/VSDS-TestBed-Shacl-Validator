package be.vlaanderen.informatievlaanderen.ldes;

import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.requests.PostRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.entity.ContentType;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.InstanceOfAssertFactories;

import java.io.UncheckedIOException;

import static org.assertj.core.api.Assertions.assertThat;

public class PostRequestAssert extends AbstractAssert<PostRequestAssert, PostRequest> {

	public PostRequestAssert(PostRequest postRequest) {
		super(postRequest, PostRequestAssert.class);
	}

	public PostRequestAssert hasUrl(String expected) {
		assertThat(actual)
				.extracting("url")
				.isEqualTo(expected);
		return this;
	}

	public PostRequestAssert hasBody(JsonNode expected) {
		assertThat(actual)
				.extracting("body", InstanceOfAssertFactories.STRING)
				.matches(actualBody -> {
					try {
						return new ObjectMapper().readTree(actualBody).equals(expected);
					} catch (JsonProcessingException e) {
						throw new UncheckedIOException(e);
					}
				});
		return this;
	}

	public PostRequestAssert hasContentType(ContentType expected) {
		assertThat(actual)
				.extracting("contentType")
				.isEqualTo(expected);
		return this;
	}
}

package be.vlaanderen.informatievlaanderen.ldes.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.http.Request;
import be.vlaanderen.informatievlaanderen.ldes.http.RequestExecutor;
import com.gitb.core.AnyContent;
import com.gitb.core.ValueEmbeddingEnumeration;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Base64;

public class StringContent {
	private final AnyContent content;

	public StringContent(@NotNull AnyContent content) {
		this.content = content;
	}

	public String getString() {
		if (content.getValue() == null) {
			return null;
		} else if (content.getEmbeddingMethod() == ValueEmbeddingEnumeration.BASE_64) {
			return new String(Base64.getDecoder().decode(content.getValue()));
		} else if (content.getEmbeddingMethod() == ValueEmbeddingEnumeration.URI) {
			final var response = new RequestExecutor().execute(new Request(content.getValue(), RequestMethod.GET));
			try {
				return EntityUtils.toString(response);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		} else {
			return content.getValue();
		}

	}

}

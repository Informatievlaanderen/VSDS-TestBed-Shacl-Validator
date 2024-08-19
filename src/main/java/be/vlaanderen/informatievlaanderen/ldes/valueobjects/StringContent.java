package be.vlaanderen.informatievlaanderen.ldes.valueobjects;

import com.gitb.core.AnyContent;
import com.gitb.core.ValueEmbeddingEnumeration;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;

public class StringContent {
	private final AnyContent content;

	public StringContent(@NotNull AnyContent content) {
		this.content = content;
	}

	public boolean isBase64Encoded() {
		return hasValueEmbeddingMethod(ValueEmbeddingEnumeration.BASE_64);
	}

	public boolean isUri() {
		return hasValueEmbeddingMethod(ValueEmbeddingEnumeration.URI);
	}

	private boolean hasValueEmbeddingMethod(ValueEmbeddingEnumeration method) {
		return content.getEmbeddingMethod() == method;
	}

	public String getString() {
		if(isBase64Encoded()) {
			return new String(Base64.getDecoder().decode(content.getValue()));
		}
		return content.getValue();
	}

}

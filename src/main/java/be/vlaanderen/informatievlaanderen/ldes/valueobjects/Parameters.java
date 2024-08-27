package be.vlaanderen.informatievlaanderen.ldes.valueobjects;

import com.gitb.core.AnyContent;
import com.gitb.core.ValueEmbeddingEnumeration;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;
import java.util.List;

public class Parameters {
	private final List<AnyContent> items;

	public Parameters(@NotNull List<AnyContent> items) {
		this.items = items;
	}

	public String getStringForName(String inputName) {
		final AnyContent item = getSingleContentForName(inputName);
		if(item.getEmbeddingMethod().equals(ValueEmbeddingEnumeration.BASE_64)) {
			return new String(Base64.getDecoder().decode(item.getValue()));
		}
		return item.getValue();
	}

	private AnyContent getSingleContentForName(String name) {
		var inputs = getInputsForName(name);
		if (inputs.isEmpty()) {
			throw new IllegalArgumentException(String.format("No input named [%s] was found.", name));
		} else if (inputs.size() > 1) {
			throw new IllegalArgumentException(String.format("Multiple inputs named [%s] were found when only one was expected.", name));
		}
		return inputs.get(0);
	}

	private List<AnyContent> getInputsForName(String name) {
		return items.stream().filter(content -> content.getName().equals(name)).toList();
	}
}

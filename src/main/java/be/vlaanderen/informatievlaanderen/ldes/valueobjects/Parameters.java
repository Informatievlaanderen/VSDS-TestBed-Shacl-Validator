package be.vlaanderen.informatievlaanderen.ldes.valueobjects;

import com.gitb.core.AnyContent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Parameters {
	private final List<AnyContent> items;

	public Parameters(@NotNull List<AnyContent> items) {
		this.items = items;
	}

	public String getString(String inputName) {
		return new StringContent(getSingleContentForName(inputName)).getString();
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

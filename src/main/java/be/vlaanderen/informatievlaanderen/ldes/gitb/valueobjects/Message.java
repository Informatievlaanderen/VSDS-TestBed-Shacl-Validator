package be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects;

import com.gitb.core.AnyContent;
import com.gitb.core.ValueEmbeddingEnumeration;

public class Message {
	private final String name;
	private final String value;

	public Message(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public AnyContent convertToAnyContent() {
		final AnyContent message = new AnyContent();
		message.setName(name);
		message.setValue("No such operation available: %s".formatted(value));
		message.setEmbeddingMethod(ValueEmbeddingEnumeration.STRING);
		return message;
	}

	public AnyContent convertToContext() {
		final AnyContent context = new AnyContent();
		context.getItem().add(convertToAnyContent());
		return context;
	}

	public static Message error(String value) {
		return new Message("ERROR", value);
	}

	public static Message info(String value) {
		return new Message("MESSAGE", value);
	}

}

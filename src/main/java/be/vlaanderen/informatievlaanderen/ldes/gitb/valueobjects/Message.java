package be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.valuebojects.ClientStatus;
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
		message.setValue(value);
		message.setEmbeddingMethod(ValueEmbeddingEnumeration.STRING);
		message.setType("string");
		return message;
	}

	public static Message error(String value) {
		return new Message("ERROR", value);
	}

	public static Message info(String value) {
		return new Message("MESSAGE", value);
	}

	public static Message statusMessage(ClientStatus status) {
		return new Message("STATUS", status.name());
	}

}

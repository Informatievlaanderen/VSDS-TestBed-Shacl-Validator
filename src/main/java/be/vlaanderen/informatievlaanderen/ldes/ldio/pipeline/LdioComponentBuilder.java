package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline;

import be.vlaanderen.informatievlaanderen.ldes.ldio.valuebojects.LdioComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valuebojects.LdioComponent;

import java.util.Map;

public abstract class LdioComponentBuilder<T extends LdioComponentBuilder<T>> {
	private final String name;
	private final Map<String, Object> properties;

	protected LdioComponentBuilder(String name, Map<String, Object> properties) {
		this.name = name;
		this.properties = properties;
	}

	protected T withProperty(String key, Object value) {
		properties.put(key, value);
		return (T) this;
	}

	public LdioComponent build() {
		return new LdioComponent(name, new LdioComponentProperties(properties));
	}
}

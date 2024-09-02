package be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.pipeline;

import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.valuebojects.LdioComponentProperties;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.valuebojects.LdioComponent;

import java.util.Map;

public abstract class LdioComponentBuilder {
	private final String name;
	private final Map<String, Object> properties;

	protected LdioComponentBuilder(String name, Map<String, Object> properties) {
		this.name = name;
		this.properties = properties;
	}

	protected void setProperty(String key, Object value) {
		properties.put(key, value);
	}

	public LdioComponent build() {
		return new LdioComponent(name, new LdioComponentProperties(properties));
	}
}

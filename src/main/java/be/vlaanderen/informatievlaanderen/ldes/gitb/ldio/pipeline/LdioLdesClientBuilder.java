package be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.pipeline;

import java.util.HashMap;
import java.util.Map;

public class LdioLdesClientBuilder extends LdioComponentBuilder {
	public LdioLdesClientBuilder() {
		super("Ldio:LdesClient",
				new HashMap<>(Map.of(
						"source-format", "text/turtle",
						"state", "sqlite"
				))
		);
	}

	public LdioLdesClientBuilder withUrl(String url) {
		setProperty("urls", url);
		return this;
	}

	public LdioLdesClientBuilder withVersionOfProperty(String versionOfProperty) {
		setProperty("materialisation", Map.of("enabled", true, "version-of-property", versionOfProperty));
		return this;
	}

	public LdioLdesClientBuilder withTimestampProperty(String timestampProperty) {
		setProperty("timestamp-path", timestampProperty);
		return this;
	}
}

package be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.pipeline;

import java.util.HashMap;
import java.util.Map;

public class LdioLdesClientBuilder extends LdioComponentBuilder {
	public LdioLdesClientBuilder() {
		super("Ldio:LdesClient", new HashMap<>(Map.of("source-format", "application/n-quads")));
	}

	public LdioLdesClientBuilder withUrl(String url) {
		setProperty("urls", url);
		return this;
	}

	public LdioLdesClientBuilder withVersionOfProperty(String versionOfProperty) {
		setProperty("materialisation", Map.of("enabled", true, "version-of-property", versionOfProperty));
		return this;
	}
}

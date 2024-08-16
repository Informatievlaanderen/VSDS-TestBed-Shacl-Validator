package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline;

import java.util.HashMap;
import java.util.Map;

public class LdioLdesClientBuilder extends LdioComponentBuilder<LdioLdesClientBuilder> {
	public LdioLdesClientBuilder() {
		super("Ldio:LdesClient", new HashMap<>(Map.of("source-format", "application/n-quads")));
	}

	public LdioLdesClientBuilder withUrl(String url) {
		return withProperty("urls", url);
	}

	public LdioLdesClientBuilder withVersionOfProperty(String versionOfProperty) {
		return withProperty("materialisation", Map.of("enabled", true, "version-of-property", versionOfProperty));
	}
}

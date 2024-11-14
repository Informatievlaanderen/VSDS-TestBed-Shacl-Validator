package be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.pipeline;

import java.util.HashMap;
import java.util.Map;

public class LdioLdesClientBuilder extends LdioComponentBuilder {
	public LdioLdesClientBuilder() {
		super("Ldio:LdesClient",
				new HashMap<>(Map.of(
						"source-format", "text/turtle",
						"state", "sqlite",
						"materialisation", Map.of("enabled", true)
				))
		);
	}

	public LdioLdesClientBuilder withUrl(String url) {
		setProperty("urls", url);
		return this;
	}
}

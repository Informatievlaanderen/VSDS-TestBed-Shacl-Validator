package be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.valuebojects;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record LdioComponent(
		String name,
		@JsonUnwrapped
		LdioComponentProperties properties
) {
}

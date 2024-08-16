package be.vlaanderen.informatievlaanderen.ldes.ldio.valuebojects;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public record LdioPipeline(
		String name,
		String description,
		LdioComponent input,
		@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
		List<LdioComponent> transformers,
		List<LdioComponent> outputs
) {
	public LdioPipeline(String name, String description, LdioComponent input, List<LdioComponent> outputs) {
		this(name, description, input, List.of(), outputs);
	}
}

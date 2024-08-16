package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline;

import be.vlaanderen.informatievlaanderen.ldes.ldio.valuebojects.LdioPipeline;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class ValidationPipelineFactory {
	private static final String PIPELINE_NAME = "validation-pipeline";
	private static final String PIPELINE_DESCRIPTION = "Pipeline that will only replicate an LDES for validation purposes";
	private final String ldesUrl;
	private final String sparqlHost;

	public ValidationPipelineFactory(String ldesUrl, String sparqlHost) {
		this.ldesUrl = ldesUrl;
		this.sparqlHost = sparqlHost;
	}

	public LdioPipeline createValidationPipeline() {
		return new LdioPipeline(
				PIPELINE_NAME,
				PIPELINE_DESCRIPTION,
				new LdioLdesClientBuilder()
						.withUrl(ldesUrl)
						.withVersionOfProperty(ldesUrl)
						.build(),
				List.of(new LdioRepositorySinkBuilder()
						.withSparqlHost(sparqlHost)
						.withRepositoryId(ldesUrl)
						.build())
		);
	}

	public String createValidationPipelineAsJson() throws JsonProcessingException {
		final LdioPipeline pipeline = createValidationPipeline();
		final ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(pipeline);
	}
}

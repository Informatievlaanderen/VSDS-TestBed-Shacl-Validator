package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline;

import be.vlaanderen.informatievlaanderen.ldes.ldes.EventStreamProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valuebojects.LdioPipeline;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class ValidationPipelineSupplier {
	private static final String PIPELINE_DESCRIPTION = "Pipeline that will only replicate an LDES for validation purposes";
	private final EventStreamProperties eventStreamProperties;
	private final String sparqlHost;
	private final String pipelineName;

	public ValidationPipelineSupplier(EventStreamProperties eventStreamProperties, String sparqlHost, String pipelineName) {
		this.eventStreamProperties = eventStreamProperties;
		this.sparqlHost = sparqlHost;
		this.pipelineName = pipelineName;
	}

	public LdioPipeline getValidationPipeline() {
		return new LdioPipeline(
				pipelineName,
				PIPELINE_DESCRIPTION,
				new LdioLdesClientBuilder()
						.withUrl(eventStreamProperties.ldesServerUrl())
						.withVersionOfProperty(eventStreamProperties.versionOfPath())
						.build(),
				List.of(new LdioRepositorySinkBuilder()
						.withSparqlHost(sparqlHost)
						.withRepositoryId(pipelineName)
						.withBatchSize(1)
						.build())
		);
	}

	public String getValidationPipelineAsJson() {
		final LdioPipeline pipeline = getValidationPipeline();
		final ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(pipeline);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Could not serialize pipeline to JSON", e);
		}
	}
}

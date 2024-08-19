package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline;

import be.vlaanderen.informatievlaanderen.ldes.ldes.valueobjects.EventStreamProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valuebojects.LdioPipeline;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class ValidationPipelineSupplier {
	public static final String PIPELINE_NAME = "validation-pipeline";
	private static final String PIPELINE_DESCRIPTION = "Pipeline that will only replicate an LDES for validation purposes";
	private final EventStreamProperties eventStreamProperties;
	private final String sparqlHost;

	public ValidationPipelineSupplier(EventStreamProperties eventStreamProperties, String sparqlHost) {
		this.eventStreamProperties = eventStreamProperties;
		this.sparqlHost = sparqlHost;
	}

	public LdioPipeline createValidationPipeline() {
		return new LdioPipeline(
				PIPELINE_NAME,
				PIPELINE_DESCRIPTION,
				new LdioLdesClientBuilder()
						.withUrl(eventStreamProperties.ldesServerUrl())
						.withVersionOfProperty(eventStreamProperties.versionOfPath())
						.build(),
				List.of(new LdioRepositorySinkBuilder()
						.withSparqlHost(sparqlHost)
						.withRepositoryId(eventStreamProperties.collectionName())
						.build())
		);
	}

	public String createValidationPipelineAsJson() {
		final LdioPipeline pipeline = createValidationPipeline();
		final ObjectMapper objectMapper = new ObjectMapper();
		try{
			return objectMapper.writeValueAsString(pipeline);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Could not serialize pipeline to JSON", e);
		}
	}
}

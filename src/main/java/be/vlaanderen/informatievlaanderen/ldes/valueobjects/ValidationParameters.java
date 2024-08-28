package be.vlaanderen.informatievlaanderen.ldes.valueobjects;

import org.eclipse.rdf4j.model.Model;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.ValidationPipelineSupplier.PIPELINE_NAME_TEMPLATE;

public record ValidationParameters(String ldesUrl, Model shaclShape, String sessionId) {
	public String pipelineName() {
		return PIPELINE_NAME_TEMPLATE.formatted(sessionId);
	}
}

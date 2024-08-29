package be.vlaanderen.informatievlaanderen.ldes.valueobjects;

import org.eclipse.rdf4j.model.Model;

public record ValidationParameters(String ldesUrl, Model shaclShape, String sessionId) {
	public static final String PIPELINE_NAME_TEMPLATE = "validation-pipeline-%s";
	public static final String REPOSITORY_ID_TEMPLATE = "validation-%s";

	public String pipelineName() {
		return PIPELINE_NAME_TEMPLATE.formatted(sessionId);
	}
}

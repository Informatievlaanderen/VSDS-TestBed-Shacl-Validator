package be.vlaanderen.informatievlaanderen.ldes.gitb.shacl.valueobjects;

import org.eclipse.rdf4j.model.Model;

public record ValidationParameters(String ldesUrl, Model shaclShape, String sessionId) {
	public static final String LDES_URL_KEY = "ldes-url";
	public static final String SHACL_SHAPE_KEY = "shacl-shape";
	public static final String PIPELINE_NAME_TEMPLATE = "validation-pipeline-%s";

	public String pipelineName() {
		return PIPELINE_NAME_TEMPLATE.formatted(sessionId);
	}
}

package be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects;

public class ProcessParameters {
	public static final String LDES_URL_KEY = "ldes-url";
	public static final String PIPELINE_NAME_TEMPLATE = "validation-pipeline-%s";
	private final String sessionId;
	private final Parameters parameters;

	public ProcessParameters(String sessionId, Parameters parameters) {
		this.sessionId = sessionId;
		this.parameters = parameters;
	}

	public String getPipelineName() {
		return PIPELINE_NAME_TEMPLATE.formatted(sessionId);
	}

	public String getLdesUrl() {
		return parameters.getStringForName(LDES_URL_KEY);
	}


}

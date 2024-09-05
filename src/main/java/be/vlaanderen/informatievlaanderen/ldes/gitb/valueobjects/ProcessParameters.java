package be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects;

import com.gitb.core.AnyContent;

import java.util.List;

public class ProcessParameters {
	public static final String LDES_URL_KEY = "ldes-url";
	private final SessionId sessionId;
	private final Parameters parameters;

	public ProcessParameters(SessionId sessionId, Parameters parameters) {
		this.sessionId = sessionId;
		this.parameters = parameters;
	}

	public ProcessParameters(String sessionId, List<AnyContent> items) {
		this(new SessionId(sessionId), new Parameters(items));
	}

	public String getPipelineName() {
		return sessionId.getPipelineName();
	}

	public String getLdesUrl() {
		return parameters.getStringForName(LDES_URL_KEY);
	}


}

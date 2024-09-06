package be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.gitb.services.RDFConverter;
import com.gitb.core.AnyContent;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.util.List;

public final class ValidationParameters {
	public static final String SHACL_SHAPE_KEY = "shacl-shape";
	public static final String PIPELINE_NAME_TEMPLATE = "validation-pipeline-%s";
	private final SessionId sessionId;
	private final Parameters parameters;

	public ValidationParameters(SessionId sessionId, Parameters parameters) {
		this.sessionId = sessionId;
		this.parameters = parameters;
	}

	public ValidationParameters(SessionId sessionId, List<AnyContent> items) {
		this(sessionId, new Parameters(items));
	}

	public String sessionId() {
		return sessionId.getId();
	}

	public String pipelineName() {
		return sessionId.getPipelineName();
	}

	public Model shaclShape() {
		String shacl = parameters.getStringForName(SHACL_SHAPE_KEY);
		return  RDFConverter.readModel(shacl, RDFFormat.TURTLE);
	}
}

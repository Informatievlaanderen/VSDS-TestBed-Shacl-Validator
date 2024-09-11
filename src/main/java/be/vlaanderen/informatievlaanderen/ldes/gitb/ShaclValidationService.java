package be.vlaanderen.informatievlaanderen.ldes.gitb;

import be.vlaanderen.informatievlaanderen.ldes.gitb.rdfrepo.RepositoryValidator;
import be.vlaanderen.informatievlaanderen.ldes.gitb.services.ValidationReportToTarMapper;
import be.vlaanderen.informatievlaanderen.ldes.gitb.shacl.valueobjects.ValidationReport;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.SessionId;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ValidationParameters;
import com.gitb.core.Metadata;
import com.gitb.core.TypedParameter;
import com.gitb.core.TypedParameters;
import com.gitb.core.ValidationModule;
import com.gitb.vs.Void;
import com.gitb.vs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ValidationParameters.SHACL_SHAPE_KEY;

/**
 * Spring component that realises the validation service.
 */
@Component
public class ShaclValidationService implements ValidationService {

	private static final Logger LOG = LoggerFactory.getLogger(ShaclValidationService.class);
	private static final String SERVICE_NAME = "LdesMemberShaclValidator";

	private final RepositoryValidator repositoryValidator;

	public ShaclValidationService(RepositoryValidator repositoryValidator) {
		this.repositoryValidator = repositoryValidator;
	}

	@Override
	public GetModuleDefinitionResponse getModuleDefinition(Void parameters) {
		final var validationModule = new ValidationModule();
		validationModule.setId(SERVICE_NAME);
		validationModule.setOperation("V");

		final var metadata = new Metadata();
		metadata.setName(SERVICE_NAME);
		validationModule.setMetadata(metadata);

		final var shaclShapeParam = new TypedParameter();
		shaclShapeParam.setName(SHACL_SHAPE_KEY);
		shaclShapeParam.setType("string");

		final var inputs = new TypedParameters();
		inputs.getParam().add(shaclShapeParam);
		validationModule.setInputs(inputs);

		final GetModuleDefinitionResponse response = new GetModuleDefinitionResponse();
		response.setModule(validationModule);
		return response;
	}

	@Override
	public ValidationResponse validate(ValidateRequest validateRequest) {
		final SessionId sessionId = SessionId.from(validateRequest.getSessionId());
		LOG.info("Received 'validate' command from test bed for session [{}]", sessionId);
		final ValidationReport validationReport = repositoryValidator.validate(new ValidationParameters(sessionId, validateRequest.getInput()));
		LOG.atInfo().log("Validation for session [{}] completed with report: {}", sessionId, validationReport);
		return buildValidationResult(validationReport);
	}

	private ValidationResponse buildValidationResult(ValidationReport validationReport) {
		final ValidationResponse result = new ValidationResponse();
		result.setReport(ValidationReportToTarMapper.mapToTar(validationReport));
		return result;
	}
}

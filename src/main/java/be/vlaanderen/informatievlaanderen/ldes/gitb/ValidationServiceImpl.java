package be.vlaanderen.informatievlaanderen.ldes.gitb;

import be.vlaanderen.informatievlaanderen.ldes.gitb.shacl.ShaclValidator;
import be.vlaanderen.informatievlaanderen.ldes.gitb.services.RDFConverter;
import be.vlaanderen.informatievlaanderen.ldes.gitb.services.ValidationReportToTarMapper;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.Parameters;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ValidationParameters;
import be.vlaanderen.informatievlaanderen.ldes.gitb.shacl.valueobjects.ValidationReport;
import com.gitb.core.Metadata;
import com.gitb.core.TypedParameter;
import com.gitb.core.TypedParameters;
import com.gitb.core.ValidationModule;
import com.gitb.vs.Void;
import com.gitb.vs.*;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ValidationParameters.LDES_URL_KEY;
import static be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ValidationParameters.SHACL_SHAPE_KEY;

/**
 * Spring component that realises the validation service.
 */
@Component
public class ValidationServiceImpl implements ValidationService {

	private static final Logger LOG = LoggerFactory.getLogger(ValidationServiceImpl.class);
	private static final String SERVICE_NAME = "LdesMemberShaclValidator";

	private final ShaclValidator shaclValidator;

	public ValidationServiceImpl(ShaclValidator shaclValidator) {
		this.shaclValidator = shaclValidator;
	}

	@Override
	public GetModuleDefinitionResponse getModuleDefinition(Void parameters) {
		final var validationModule = new ValidationModule();
		validationModule.setId(SERVICE_NAME);
		validationModule.setOperation("V");

		final var metadata = new Metadata();
		metadata.setName(SERVICE_NAME);
		validationModule.setMetadata(metadata);

		final var ldesServerParam = new TypedParameter();
		ldesServerParam.setName(LDES_URL_KEY);
		ldesServerParam.setType("string");

		final var shaclShapeParam = new TypedParameter();
		shaclShapeParam.setName(SHACL_SHAPE_KEY);
		shaclShapeParam.setType("string");

		final var inputs = new TypedParameters();
		inputs.getParam().addAll(List.of(ldesServerParam, shaclShapeParam));
		validationModule.setInputs(inputs);

		GetModuleDefinitionResponse response = new GetModuleDefinitionResponse();
		response.setModule(validationModule);
		return response;
	}

	@Override
	public ValidationResponse validate(ValidateRequest validateRequest) {
		final ValidationParameters validationParams = extractValidationParamsFromRequest(validateRequest);
		LOG.info("Received 'validate' command from test bed for session [{}]", validationParams.sessionId());
		final ValidationReport validationReport = shaclValidator.validate(validationParams);
		LOG.atInfo().log("Validation for session [{}] completed with report: {}", validationParams.sessionId(), validationReport);
		return buildValidationResult(validationReport);
	}

	private ValidationParameters extractValidationParamsFromRequest(ValidateRequest validateRequest) {
		final Parameters params = new Parameters(validateRequest.getInput());
		final String sessionId = validateRequest.getSessionId() != null ? validateRequest.getSessionId() : UUID.randomUUID().toString();
		String shacl = params.getStringForName(SHACL_SHAPE_KEY);
		String url = params.getStringForName(LDES_URL_KEY);
		final Model shaclShape = RDFConverter.readModel(shacl, RDFFormat.TURTLE);
		return new ValidationParameters(url, shaclShape, sessionId);
	}

	private ValidationResponse buildValidationResult(ValidationReport validationReport) {
		final ValidationResponse result = new ValidationResponse();
		result.setReport(ValidationReportToTarMapper.mapToTar(validationReport));
		return result;
	}


}

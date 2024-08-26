package be.vlaanderen.informatievlaanderen.ldes.gitb;

import be.vlaanderen.informatievlaanderen.ldes.handlers.ShaclValidationHandler;
import be.vlaanderen.informatievlaanderen.ldes.services.RDFConverter;
import be.vlaanderen.informatievlaanderen.ldes.services.ValidationReportToTarMapper;
import be.vlaanderen.informatievlaanderen.ldes.valueobjects.Parameters;
import be.vlaanderen.informatievlaanderen.ldes.valueobjects.ValidationReport;
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

/**
 * Spring component that realises the validation service.
 */
@Component
public class ValidationServiceImpl implements ValidationService {

	private static final Logger LOG = LoggerFactory.getLogger(ValidationServiceImpl.class);
	private static final String SERVICE_NAME = "LdesMemberShaclValidator";

	private final ShaclValidationHandler shaclValidationHandler;

	public ValidationServiceImpl(ShaclValidationHandler shaclValidationHandler) {
		this.shaclValidationHandler = shaclValidationHandler;

	}

	/**
	 * The purpose of the getModuleDefinition call is to inform its caller on how the service is supposed to be called.
	 * <p/>
	 * Note that defining the implementation of this service is optional, and can be empty unless you plan to publish
	 * the service for use by third parties (in which case it serves as documentation on its expected inputs and outputs).
	 *
	 * @param parameters No parameters are expected.
	 * @return The response.
	 */
//	@Override
//	public GetModuleDefinitionResponse getModuleDefinition(Void parameters) {
//		final var validationModule = new ValidationModule();
//		validationModule.setId(SERVICE_NAME);
//		validationModule.setOperation("V");
//
//		final var metadata = new Metadata();
//		metadata.setName(SERVICE_NAME);
//		validationModule.setMetadata(metadata);
//
//		final var ldesServerParam = new TypedParameter();
//		ldesServerParam.setName("server-url");
//		ldesServerParam.setType("string");
//
//		final var shaclShapeParam = new TypedParameter();
//		shaclShapeParam.setName("shacl-shape");
//		shaclShapeParam.setType("string");
//
//		final var inputs = new TypedParameters();
//		inputs.getParam().addAll(List.of(ldesServerParam, shaclShapeParam));
//		validationModule.setInputs(inputs);
//
//		GetModuleDefinitionResponse response = new GetModuleDefinitionResponse();
//		response.setModule(validationModule);
//		return response;
//	}

	@Override
	public GetModuleDefinitionResponse getModuleDefinition(Void parameters) {
		GetModuleDefinitionResponse response = new GetModuleDefinitionResponse();
		response.setModule(new ValidationModule());
		response.getModule().setId(SERVICE_NAME);
//        response.getModule().setConfigs();
		return response;
	}

	/**
	 * The validate operation is called to validate the input and produce a validation report.
	 * <p>
	 * The expected input is described for the service's client through the getModuleDefinition call.
	 *
	 * @param validateRequest The input parameters and configuration for the validation.
	 * @return The response containing the validation report.
	 */
	@Override
	public ValidationResponse validate(ValidateRequest validateRequest) {
		LOG.info("Received 'validate' command from test bed for session [{}]", validateRequest.getSessionId());
		ValidationResponse result = new ValidationResponse();
		// First extract the parameters and check to see if they are as expected.
		final Parameters params = new Parameters(validateRequest.getInput());
		String shacl = params.getString("shacl-shape");
		String url = params.getString("server-url");

		final Model shaclShape = RDFConverter.readModel(shacl, RDFFormat.TURTLE);
		final ValidationReport validationReport = shaclValidationHandler.validate(url, shaclShape);
		result.setReport(ValidationReportToTarMapper.mapToTar(validationReport));
		return result;
	}


}

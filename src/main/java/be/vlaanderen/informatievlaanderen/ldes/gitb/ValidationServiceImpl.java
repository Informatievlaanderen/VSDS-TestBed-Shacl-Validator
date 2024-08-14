package be.vlaanderen.informatievlaanderen.ldes.gitb;

import be.vlaanderen.informatievlaanderen.ldes.handlers.ShaclValidationHandler;
import be.vlaanderen.informatievlaanderen.ldes.services.RDFConverter;
import be.vlaanderen.informatievlaanderen.ldes.services.ValidationReportToTarMapper;
import be.vlaanderen.informatievlaanderen.ldes.valueobjects.ValidationReport;
import com.gitb.core.ValidationModule;
import com.gitb.vs.Void;
import com.gitb.vs.*;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Spring component that realises the validation service.
 */
@Component
public class ValidationServiceImpl implements ValidationService {

	private static final Logger LOG = LoggerFactory.getLogger(ValidationServiceImpl.class);
	private static final String SERVICE_NAME = "LdesmemberShaclValidator";

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
	 * @param parameters The input parameters and configuration for the validation.
	 * @return The response containing the validation report.
	 */
	@Override
	public ValidationResponse validate(ValidateRequest parameters) {
		LOG.info("Received 'validate' command from test bed for session [{}]", parameters.getSessionId());
		ValidationResponse result = new ValidationResponse();
		// First extract the parameters and check to see if they are as expected.
		String shacl = Utils.getRequiredString(parameters.getInput(), "shacl-shape");
		String url = Utils.getRequiredString(parameters.getInput(), "server-url");

		final Model shaclShape = RDFConverter.readModel(shacl, RDFFormat.TURTLE);
		final ValidationReport validationReport = shaclValidationHandler.validate(url, shaclShape);
		result.setReport(ValidationReportToTarMapper.mapToTar(validationReport));
		return result;
	}


}

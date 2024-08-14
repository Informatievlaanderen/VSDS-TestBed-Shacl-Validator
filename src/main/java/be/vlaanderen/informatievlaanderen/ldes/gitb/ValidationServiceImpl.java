package be.vlaanderen.informatievlaanderen.ldes.gitb;

import be.vlaanderen.informatievlaanderen.ldes.handlers.ShaclValidationHandler;
import be.vlaanderen.informatievlaanderen.ldes.services.RDFConverter;
import com.gitb.core.ValidationModule;
import com.gitb.tr.TAR;
import com.gitb.tr.TestAssertionGroupReportsType;
import com.gitb.tr.TestResultType;
import com.gitb.tr.ValidationCounters;
import com.gitb.vs.Void;
import com.gitb.vs.*;
import com.google.common.collect.Iterables;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

import static be.vlaanderen.informatievlaanderen.ldes.constants.RDFConstants.*;

/**
 * Spring component that realises the validation service.
 */
@Component
public class ValidationServiceImpl implements ValidationService {

    private static final String SERVICE_NAME = "LdesmemberShaqlValidator";
    @Autowired
    private ShaclValidationHandler shaclValidationHandler;

    /** Logger. **/
    private static final Logger LOG = LoggerFactory.getLogger(ValidationServiceImpl.class);

    @Autowired
    private Utils utils = null;

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
     *
     * The expected input is described for the service's client through the getModuleDefinition call.
     *
     * @param parameters The input parameters and configuration for the validation.
     * @return The response containing the validation report.
     */
    @Override
    public ValidationResponse validate(ValidateRequest parameters) {
        LOG.info("Received 'validate' command from test bed for session [{}]", parameters.getSessionId());
        ValidationResponse result = new ValidationResponse();
        TAR report = utils.createReport(TestResultType.SUCCESS);
        // First extract the parameters and check to see if they are as expected.
        String shacl = utils.getRequiredString(parameters.getInput(), "shacl-shape");
        String url = utils.getRequiredString(parameters.getInput(), "server-url");

        report.setReports(new TestAssertionGroupReportsType());
        int infos = 0;
        int warnings = 0;
        int errors = 0;

        try {
            Model shaclModel = RDFConverter.readModel(shacl, RDFFormat.TURTLE);
            Model validationReport = shaclValidationHandler.validate(url, shaclModel);
            int count;
            if ((count = getErrorCount(validationReport)) > 0) {
                errors += count;
                utils.addReportItemError(RDFConverter.writeModel(validationReport, RDFFormat.TURTLE), report.getReports().getInfoOrWarningOrError());
            } else if ((count = getWarnCount(validationReport)) > 0) {
                warnings += count;
                utils.addReportItemWarning(RDFConverter.writeModel(validationReport, RDFFormat.TURTLE), report.getReports().getInfoOrWarningOrError());
            } else if ((count = getInfoCount(validationReport)) > 0) {
                infos += count;
                utils.addReportItemInfo(RDFConverter.writeModel(validationReport, RDFFormat.TURTLE), report.getReports().getInfoOrWarningOrError());
            }

        } catch (Exception e) {

        }

        report.setCounters(new ValidationCounters());
        report.getCounters().setNrOfAssertions(BigInteger.valueOf(infos));
        report.getCounters().setNrOfWarnings(BigInteger.valueOf(warnings));
        report.getCounters().setNrOfErrors(BigInteger.valueOf(errors));
        if (errors > 0) {
            report.setResult(TestResultType.FAILURE);
        } else if (warnings > 0) {
            report.setResult(TestResultType.WARNING);
        }
        // Return the report.
        result.setReport(report);
        return result;
    }

    private int getErrorCount(Model report) {
        return Iterables.size(report.getStatements(null, SEVERITY, VIOLATION));
    }

    private int getWarnCount(Model report) {
        return Iterables.size(report.getStatements(null, SEVERITY, WARNING));
    }

    private int getInfoCount(Model report) {
        return Iterables.size(report.getStatements(null, SEVERITY, INFO));
    }
}

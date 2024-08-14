package be.vlaanderen.informatievlaanderen.ldes.gitb;

import be.vlaanderen.informatievlaanderen.ldes.valueobjects.Parameters;
import com.gitb.core.ValueEmbeddingEnumeration;
import com.gitb.ps.Void;
import com.gitb.ps.*;
import com.gitb.tr.TestResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Spring component that realises the processing service.
 */
@Component
public class ProcessingServiceImpl implements ProcessingService {

    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(ProcessingServiceImpl.class);

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
        return new GetModuleDefinitionResponse();
    }

    /**
     * The purpose of the process operation is to execute one of the service's supported operations.
     * <p/>
     * What would typically take place here is as follows:
     * <ol>
     *    <li>Check that the requested operation is indeed supported by the service.</li>
     *    <li>For the requested operation collect and check the provided input parameters.</li>
     *    <li>Perform the requested operation and return the result to the test bed.</li>
     * </ol>
     *
     * @param processRequest The requested operation and input parameters.
     * @return The result.
     */
    @Override
    public ProcessResponse process(ProcessRequest processRequest) {
        LOG.info("Received 'process' command from test bed for session [{}]", processRequest.getSessionId());
        ProcessResponse response = new ProcessResponse();
        response.setReport(Utils.createReport(TestResultType.SUCCESS));
        String operation = processRequest.getOperation();
        if (operation == null) {
            throw new IllegalArgumentException("No processing operation provided");
        }
        String input = new Parameters(processRequest.getInput()).getString("input");
        String result = switch (operation) {
            case "uppercase" -> input.toUpperCase();
            case "lowercase" -> input.toLowerCase();
            default -> throw new IllegalArgumentException(String.format("Unexpected operation [%s].", operation));
        };
        response.getOutput().add(Utils.createAnyContentSimple("output", result, ValueEmbeddingEnumeration.STRING));
        LOG.info("Completed operation [{}]. Input was [{}], output was [{}].", operation, input, result);
        return response;
    }

    /**
     * The purpose of the beginTransaction operation is to begin a unique processing session.
     * <p/>
     * Transactions are used when processing services need to maintain state across several calls. If this is needed
     * then this implementation would generate a session identifier and record the session for subsequent 'process' calls.
     * <p/>
     * In the typical case where no state needs to be maintained, you can provide an empty implementation for this method.
     *
     * @param beginTransactionRequest Optional configuration parameters to consider when starting a processing transaction.
     * @return The response with the generated session ID for the processing transaction.
     */
    @Override
    public BeginTransactionResponse beginTransaction(BeginTransactionRequest beginTransactionRequest) {
        return new BeginTransactionResponse();
    }

    /**
     * The purpose of the endTransaction operation is to complete an ongoing processing session.
     * <p/>
     * The main actions to be taken as part of this operation are to remove the provided session identifier (if this
     * was being recorded to begin with), and to perform any custom cleanup tasks.
     *
     * @param parameters The identifier of the session to terminate.
     * @return A void response.
     */
    @Override
    public Void endTransaction(BasicRequest parameters) {
        return new Void();
    }

}

package be.vlaanderen.informatievlaanderen.ldes.gitb;

import com.gitb.ms.Void;
import com.gitb.ms.*;
import com.gitb.tr.TestResultType;
import jakarta.annotation.Resource;
import jakarta.xml.ws.WebServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Spring component that realises the messaging service.
 */
@Component
public class MessagingServiceImpl implements MessagingService {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(MessagingServiceImpl.class);

	private final StateManager stateManager;
	private final Utils utils;

	@Resource
	private WebServiceContext wsContext;

	public MessagingServiceImpl(StateManager stateManager, Utils utils) {
		this.stateManager = stateManager;
		this.utils = utils;
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
		return new GetModuleDefinitionResponse();
	}

	/**
	 * The initiate operation is called by the test bed when a new test session is being prepared.
	 * <p/>
	 * This call expects from the service to do the following:
	 * <ul>
	 *     <li>Record the session identifier to keep track of messages linked to the test session.</li>
	 *     <li>Process, if needed, the configuration provided by the SUT.</li>
	 *     <li>Return, if needed, configuration to be displayed to the user for the SUT actor.</li>
	 * </ul>
	 *
	 * @param parameters The actor configuration provided by the SUT.
	 * @return The session ID and any generated configuration to display for the SUT.
	 */
	@Override
	public InitiateResponse initiate(InitiateRequest parameters) {
		InitiateResponse response = new InitiateResponse();
		// Get the ReplyTo address for the test bed callbacks based on WS-Addressing.
		String replyToAddress = utils.getReplyToAddressFromHeaders(wsContext).orElseThrow();
		// Get the test session ID to use for tracking session state.
		String sessionId = utils.getTestSessionIdFromHeaders(wsContext).orElseThrow();
		stateManager.createSession(sessionId, replyToAddress);
		LOG.info("Initiated a new session [{}] with callback address [{}]", sessionId, replyToAddress);
		return response;
	}

	/**
	 * The receive operation is called when the test bed is expecting to receive a message.
	 * <p/>
	 * The goal here is to be informed by the test bed on the characteristics of the message we are expecting to receive.
	 * These characteristics would need to be recorded as part of this operation in the service's session state so
	 * that incoming messages can be matched against them. Once the expected message is received, the TestBedNotifier
	 * can then be used to ping the test bed.
	 * <p/>
	 * Besides the expected message's characteristics, the service should also record:
	 * <ul>
	 *     <li>The test session identifier.</li>
	 *     <li>The call identifier (the identifier of the relevant 'receive' step that resulted in this call).</li>
	 *     <li>The callback address of the test bed (this could also be fixed as a configuration property).</li>
	 * </ul>
	 *
	 * @param parameters The input parameters to consider (if any).
	 * @return A void result.
	 */
	@Override
	public Void receive(ReceiveRequest parameters) {
		LOG.info("Received 'receive' command from test bed for session [{}]", parameters.getSessionId());
		return new Void();
	}

	/**
	 * The send operation is called when the test bed wants to send a message through this service.
	 * <p/>
	 * This is the point where input is received for the call that this service needs to translate into an actual
	 * communication. This communication would be specific to a communication protocol or a separate system's API.
	 * <p/>
	 * The result of the operation is typically an empty success or failure report depending on whether or not the
	 * communication was successful. This report could however include additional information that would be reported
	 * back to the test bed.
	 *
	 * @param parameters The input parameters and configuration to consider for the send operation.
	 * @return A status report for the call that will be returned to the test bed.
	 */
	@Override
	public SendResponse send(SendRequest parameters) {
		LOG.info("Received 'send' command from test bed for session [{}]", parameters.getSessionId());
        /*
        At this point we would expect the actual communication or simulation to take place. In this sample implementation
        we simply log the message received from the test bed.
         */
		String messageToSend = utils.getRequiredString(parameters.getInput(), "messageToSend");
		LOG.info("The message to send is [{}]", messageToSend);
		SendResponse response = new SendResponse();
		response.setReport(utils.createReport(TestResultType.SUCCESS));
		return response;
	}

	/**
	 * The beginTransaction operation is called by the test bed with a transaction starts.
	 * <p/>
	 * Often there is no need to take any action here but it could be interesting to do so if you need specific
	 * actions per transaction.
	 *
	 * @param parameters The transaction configuration.
	 * @return A void result.
	 */
	@Override
	public Void beginTransaction(BeginTransactionRequest parameters) {
		LOG.info("Transaction starting for session [{}]", parameters.getSessionId());
		return new Void();
	}

	/**
	 * The endTransaction operation is the counterpart of the beginTransaction and is called when the transaction terminates.
	 *
	 * @param parameters The session ID this transaction related to.
	 * @return A void result.
	 */
	@Override
	public Void endTransaction(BasicRequest parameters) {
		LOG.info("Transaction ending for session [{}]", parameters.getSessionId());
		return new Void();
	}

	/**
	 * The finalize operation is called by the test bed when a test session completes.
	 * <p/>
	 * A typical action that needs to take place here is the cleanup of any resources that were specific to the session
	 * in question. This would typically involve the state recorded for the session.
	 *
	 * @param parameters The session ID that completed.
	 * @return A void result.
	 */
	@Override
	public Void finalize(FinalizeRequest parameters) {
		LOG.info("Finalising session [{}]", parameters.getSessionId());
		// Cleanup in-memory state for the completed session.
		stateManager.destroySession(parameters.getSessionId());
		return new Void();
	}

}

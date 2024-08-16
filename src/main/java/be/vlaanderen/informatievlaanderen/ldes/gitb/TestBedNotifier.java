package be.vlaanderen.informatievlaanderen.ldes.gitb;

import be.vlaanderen.informatievlaanderen.ldes.gitb.services.MessagingClients;
import com.gitb.core.LogLevel;
import com.gitb.ms.LogRequest;
import com.gitb.ms.NotifyForMessageRequest;
import com.gitb.tr.TAR;
import com.gitb.tr.TestResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Component used to notify the Test Bed of received queries.
 * <p/>
 * The main reason of defining this as a separate component is to facilitate making these notifications asynchronous
 * (see the notifyTestBed method that is marked as async).
 * <p/>
 * As an example, the configuration of a proxy to be used for this call is provided that can be optionally set on the
 * call-back service proxy via configuration properties (set in application.properties).
 */
@Component
public class TestBedNotifier {

	private static final Logger LOG = LoggerFactory.getLogger(TestBedNotifier.class);
	private final MessagingClients messagingClients;

	public TestBedNotifier(MessagingClients messagingClients) {
		this.messagingClients = messagingClients;
	}


	/**
	 * Send a log message to the Test Bed at a given severity level.
	 *
	 * @param sessionId       The session identifier.
	 * @param callbackAddress The Test Bed's callback address to use.
	 * @param message         The log message.
	 * @param level           The severity level.
	 */
	@Async
	public void sendLogMessage(String sessionId, String callbackAddress, String message, LogLevel level) {
		var logRequest = new LogRequest();
		logRequest.setSessionId(sessionId);
		logRequest.setMessage(message);
		logRequest.setLevel(level);
		messagingClients.getOrCreateByCallbackAddress(callbackAddress).log(logRequest);
	}

	/**
	 * Notify the Test Bed for a given session.
	 *
	 * @param sessionId The session ID to notify the test bed for.
	 * @param callId    The 'receive' call ID to notify the Test Bed for.
	 * @param report    The report to notify the Test Bed with.
	 */
	@Async
	public void notifyTestBed(String sessionId, String callId, String callback, TAR report) {
		try {
			LOG.info("Notifying Test Bed for session [{}]", sessionId);
			callTestBed(sessionId, callId, report, callback);
		} catch (Exception e) {
			LOG.warn("Error while notifying test bed for session [{}]", sessionId, e);
			callTestBed(sessionId, callId, Utils.createReport(TestResultType.FAILURE), callback);
		}
	}

	/**
	 * Call the Test Bed to notify it of received communication.
	 *
	 * @param sessionId       The session ID that this notification relates to.
	 * @param callId          The 'receive' call ID to notify the test bed for.
	 * @param report          The TAR report to send back.
	 * @param callbackAddress The address on which the call is to be made.
	 */
	private void callTestBed(String sessionId, String callId, TAR report, String callbackAddress) {
		// Make the call.
		NotifyForMessageRequest request = new NotifyForMessageRequest();
		request.setSessionId(sessionId);
		request.setCallId(callId);
		request.setReport(report);
		messagingClients.getOrCreateByCallbackAddress(callbackAddress).notifyForMessage(request);
	}

}
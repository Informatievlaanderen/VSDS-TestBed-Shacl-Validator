package be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.gitb.services.suppliers.TarSupplier;
import com.gitb.core.AnyContent;
import com.gitb.core.ValueEmbeddingEnumeration;
import com.gitb.ps.ProcessResponse;
import com.gitb.tr.TestResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class ProcessResult {
	private static final Logger log = LoggerFactory.getLogger(ProcessResult.class);
	private final TestResultType type;
	private final Message[] messages;

	public ProcessResult(TestResultType type, Message... messages) {
		this.type = type;
		this.messages = messages;
	}

	public ProcessResponse convertToResponse() {
		final ProcessResponse response = new ProcessResponse();
		final AnyContent context = new AnyContent();
		context.setEmbeddingMethod(ValueEmbeddingEnumeration.STRING);
		context.setType("string");
		context.getItem().addAll(Arrays.stream(messages).map(Message::convertToAnyContent).toList());
		response.setReport(new TarSupplier(type, context).get());
		response.getOutput().addAll(Arrays.stream(messages).map(Message::convertToAnyContent).toList());
		return response;
	}

	public static ProcessResult invalidOperation(String name) {
		final String value = "No such operation available: %s".formatted(name);
		log.atInfo().log(value);
		return new ProcessResult(TestResultType.FAILURE, Message.error(value));
	}

	public static ProcessResult infoMessage(String messageValue) {
		return new ProcessResult(TestResultType.SUCCESS, Message.info(messageValue));
	}

}

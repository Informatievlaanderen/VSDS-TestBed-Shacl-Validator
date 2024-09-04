package be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.gitb.services.suppliers.TarSupplier;
import com.gitb.ps.ProcessResponse;
import com.gitb.tr.TestResultType;

public class ProcessResult {
	private final TestResultType type;
	private final Message message;

	public ProcessResult(TestResultType type, Message message) {
		this.type = type;
		this.message = message;
	}

	public ProcessResponse convertToResponse() {
		final ProcessResponse response = new ProcessResponse();
		response.setReport(new TarSupplier(type, message.convertToContext()).get());
		return response;
	}

	public static ProcessResult invalidOperation(String name) {
		final String value = "No such operation available: %s".formatted(name);
		return new ProcessResult(TestResultType.FAILURE, Message.error(value));
	}

	public static ProcessResult infoMessage(String messageValue) {
		return new ProcessResult(TestResultType.SUCCESS, Message.info(messageValue));
	}

}

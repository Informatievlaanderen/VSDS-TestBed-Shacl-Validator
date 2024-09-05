package be.vlaanderen.informatievlaanderen.ldes.gitb.services.replication;

import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.LdesClientStatusManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.valuebojects.ClientStatus;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.Message;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ParameterDefinition;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessParameters;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessResult;
import com.gitb.tr.TestResultType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(CheckReplicatingStatusProcessExecutor.NAME)
public class CheckReplicatingStatusProcessExecutor implements ProcessExecutor {
	public static final String NAME = "checkReplicatingStatus";
	private final LdesClientStatusManager ldesClientStatusManager;

	public CheckReplicatingStatusProcessExecutor(LdesClientStatusManager ldesClientStatusManager) {
		this.ldesClientStatusManager = ldesClientStatusManager;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public List<ParameterDefinition> getParameterDefinitions() {
		return List.of();
	}

	@Override
	public ProcessResult execute(ProcessParameters processParameters) {
		final ClientStatus status = ldesClientStatusManager.getClientStatus(processParameters.getPipelineName());
		if(ClientStatus.isSuccessfullyReplicated(status)) {
			return new ProcessResult(TestResultType.SUCCESS, new Message("STATUS", status.name()));
		}
		return new ProcessResult(
				TestResultType.WARNING,
				new Message("STATUS", status.name()),
				new Message("MESSAGE", "CLIENT is not %s or %s yet".formatted(ClientStatus.SYNCHRONISING, ClientStatus.COMPLETED))
		);
	}
}

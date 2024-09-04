package be.vlaanderen.informatievlaanderen.ldes.gitb.services.replication;

import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.LdesClientStatusManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.valuebojects.ClientStatus;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.Message;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessParameters;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessResult;
import com.gitb.tr.TestResultType;
import org.springframework.stereotype.Component;

@Component(CheckReplicatingStatusProcessExecutor.NAME)
public class CheckReplicatingStatusProcessExecutor implements ProcessExecutor {
	public static final String NAME = "checkReplicatingStatus";
	private final LdesClientStatusManager ldesClientStatusManager;

	public CheckReplicatingStatusProcessExecutor(LdesClientStatusManager ldesClientStatusManager) {
		this.ldesClientStatusManager = ldesClientStatusManager;
	}

	@Override
	public ProcessResult execute(ProcessParameters processParameters) {
		final ClientStatus status = ldesClientStatusManager.getClientStatus(processParameters.getPipelineName());
		return new ProcessResult(TestResultType.SUCCESS, new Message("CLIENT STATUS", status.name()));
	}
}

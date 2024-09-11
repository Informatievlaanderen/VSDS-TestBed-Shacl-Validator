package be.vlaanderen.informatievlaanderen.ldes.gitb.services.replication;

import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.LdesClientStatusManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.LdioPipelineManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.excpeptions.LdesClientStatusUnavailableException;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.valuebojects.ClientStatus;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.Message;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ParameterDefinition;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessParameters;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessResult;
import com.gitb.tr.TestResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(HaltWhenReplicatedProcessExecutor.NAME)
public class HaltWhenReplicatedProcessExecutor implements ProcessExecutor {
	public static final String NAME = "haltWhenReplicated";
	private static final Logger log = LoggerFactory.getLogger(HaltWhenReplicatedProcessExecutor.class);
	private final LdesClientStatusManager ldesClientStatusManager;
	private final LdioPipelineManager ldioPipelineManager;

	public HaltWhenReplicatedProcessExecutor(LdesClientStatusManager ldesClientStatusManager, LdioPipelineManager ldioPipelineManager) {
		this.ldesClientStatusManager = ldesClientStatusManager;
		this.ldioPipelineManager = ldioPipelineManager;
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
		final ClientStatus status;
		try {
			status = ldesClientStatusManager.getClientStatus(processParameters.getPipelineName());
		} catch (LdesClientStatusUnavailableException e) {
			log.atWarn().log("Client status unavailable");
			return new ProcessResult(
					TestResultType.FAILURE,
					Message.error(e.getMessage())
			);
		}
		if(ClientStatus.isSuccessfullyReplicated(status)) {
			ldioPipelineManager.haltPipeline(processParameters.getPipelineName());
			log.atInfo().log("REPLICATING has been finished for pipeline {}", processParameters.getPipelineName());
			return new ProcessResult(
					TestResultType.SUCCESS,
					Message.statusMessage(status),
					new Message("MESSAGE", "PIPELINE will be PAUSED soon")
			);
		}
		log.atDebug().log("Pipeline {} is still REPLICATING", processParameters.getPipelineName());
		return new ProcessResult(
				TestResultType.SUCCESS,
				Message.statusMessage(status),
				new Message("MESSAGE", "CLIENT is not %s or %s yet".formatted(ClientStatus.SYNCHRONISING, ClientStatus.COMPLETED))
		);
	}
}

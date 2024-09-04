package be.vlaanderen.informatievlaanderen.ldes.gitb.services.replication;

import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.LdioPipelineManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.rdfrepo.Rdf4jRepositoryManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.Message;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessParameters;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessResult;
import com.gitb.tr.TestResultType;
import org.springframework.stereotype.Component;

@Component(StartReplicatingProcessExecutor.NAME)
public class StartReplicatingProcessExecutor implements ProcessExecutor {
	public static final String NAME = "startReplicating";

	private final LdioPipelineManager ldioPipelineManager;
	private final Rdf4jRepositoryManager repositoryManager;

	public StartReplicatingProcessExecutor(LdioPipelineManager ldioPipelineManager, Rdf4jRepositoryManager repositoryManager) {
		this.ldioPipelineManager = ldioPipelineManager;
		this.repositoryManager = repositoryManager;
	}

	@Override
	public ProcessResult execute(ProcessParameters processParameters) {
		repositoryManager.createRepository(processParameters.getPipelineName());
		ldioPipelineManager.initPipeline(processParameters.getLdesUrl(), processParameters.getPipelineName());
		return new ProcessResult(TestResultType.SUCCESS, Message.info("Pipeline %s created".formatted(processParameters.getPipelineName())));
	}
}

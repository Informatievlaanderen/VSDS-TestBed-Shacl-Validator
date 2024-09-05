package be.vlaanderen.informatievlaanderen.ldes.gitb.services.replication;

import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.LdioPipelineManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.rdfrepo.Rdf4jRepositoryManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ParameterDefinition;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessParameters;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessResult;
import org.springframework.stereotype.Component;

import java.util.List;

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
	public String getName() {
		return NAME;
	}

	@Override
	public List<ParameterDefinition> getParameterDefinitions() {
		return List.of(
				new ParameterDefinition("ldes-url", "string", true, "URL of the LDES to validate")
		);
	}

	@Override
	public ProcessResult execute(ProcessParameters processParameters) {
		final String pipelineName = processParameters.getPipelineName();
		repositoryManager.createRepository(pipelineName);
		ldioPipelineManager.initPipeline(processParameters.getLdesUrl(), pipelineName);
		return ProcessResult.infoMessage("Pipeline '%s' created".formatted(pipelineName));
	}
}

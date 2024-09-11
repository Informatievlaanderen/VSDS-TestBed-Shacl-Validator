package be.vlaanderen.informatievlaanderen.ldes.gitb.services.replication;

import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.LdioPipelineManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.rdfrepo.Rdf4jRepositoryManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ParameterDefinition;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessParameters;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(DestroyPipelineProcessExecutor.NAME)
public class DestroyPipelineProcessExecutor implements ProcessExecutor {
	public static final String NAME = "destroyPipeline";
	private final LdioPipelineManager ldioPipelineManager;
	private final Rdf4jRepositoryManager rdf4jRepositoryManager;

	public DestroyPipelineProcessExecutor(LdioPipelineManager ldioPipelineManager, Rdf4jRepositoryManager rdf4jRepositoryManager) {
		this.ldioPipelineManager = ldioPipelineManager;
		this.rdf4jRepositoryManager = rdf4jRepositoryManager;
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
		ldioPipelineManager.deletePipeline(processParameters.getPipelineName());
		rdf4jRepositoryManager.deleteRepository(processParameters.getPipelineName());
		return ProcessResult.infoMessage("Pipeline '%s' is deleted".formatted(processParameters.getPipelineName()));
	}
}

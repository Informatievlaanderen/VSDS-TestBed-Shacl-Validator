package be.vlaanderen.informatievlaanderen.ldes.gitb.shacl;

import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.LdesClientStatusManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.LdioPipelineManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.rdfrepo.Rdf4jRepositoryManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.rdfrepo.RepositoryValidator;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ValidationParameters;
import be.vlaanderen.informatievlaanderen.ldes.gitb.shacl.valueobjects.ValidationReport;
import org.eclipse.rdf4j.model.Model;
import org.springframework.stereotype.Component;

import static be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ValidationParameters.PIPELINE_NAME_TEMPLATE;

@Component
public class ShaclValidator {
	private final LdioPipelineManager ldioPipelineManager;
	private final LdesClientStatusManager clientStatusManager;
	private final Rdf4jRepositoryManager repositoryManager;
	private final RepositoryValidator validator;

	public ShaclValidator(LdioPipelineManager ldioPipelineManager, LdesClientStatusManager clientStatusManager, Rdf4jRepositoryManager repositoryManager, RepositoryValidator validator) {
		this.ldioPipelineManager = ldioPipelineManager;
		this.clientStatusManager = clientStatusManager;
		this.repositoryManager = repositoryManager;
		this.validator = validator;
	}

	public ValidationReport validate(ValidationParameters params) {
		setupPipelineAndTripleStore(params);
		haltPipelineWhenReady(params);
		final Model shaclValidationReport = validator.validate(params.pipelineName(), params.shaclShape());
		cleanup(params);
		return new ValidationReport(shaclValidationReport);
	}

	private void setupPipelineAndTripleStore(ValidationParameters params) {
		repositoryManager.createRepository(params.pipelineName());
		ldioPipelineManager.initPipeline(params.ldesUrl(), params.pipelineName());
	}

	private void haltPipelineWhenReady(ValidationParameters params) {
		clientStatusManager.waitUntilReplicated(PIPELINE_NAME_TEMPLATE.formatted(params.sessionId()));
		ldioPipelineManager.haltPipeline(params.pipelineName());
	}

	private void cleanup(ValidationParameters params) {
		repositoryManager.deleteRepository(params.pipelineName());
		ldioPipelineManager.deletePipeline(params.pipelineName());
	}
}

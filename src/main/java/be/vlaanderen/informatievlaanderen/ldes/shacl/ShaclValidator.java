package be.vlaanderen.informatievlaanderen.ldes.shacl;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdesClientStatusManager;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioPipelineManager;
import be.vlaanderen.informatievlaanderen.ldes.rdfrepo.Rdf4jRepositoryManager;
import be.vlaanderen.informatievlaanderen.ldes.rdfrepo.RepositoryValidator;
import be.vlaanderen.informatievlaanderen.ldes.valueobjects.ValidationParameters;
import be.vlaanderen.informatievlaanderen.ldes.valueobjects.ValidationReport;
import org.eclipse.rdf4j.model.Model;
import org.springframework.stereotype.Component;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.ValidationPipelineSupplier.PIPELINE_NAME_TEMPLATE;

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
		repositoryManager.createRepository();
		ldioPipelineManager.initPipeline(params.ldesUrl(), params.pipelineName());
		clientStatusManager.waitUntilReplicated(PIPELINE_NAME_TEMPLATE.formatted(params.sessionId()));
		ldioPipelineManager.deletePipeline(params.pipelineName());
		final Model shaclValidationReport = validator.validate(params.shaclShape());
		repositoryManager.deleteRepository();
		return new ValidationReport(shaclValidationReport);
	}
}

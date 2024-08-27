package be.vlaanderen.informatievlaanderen.ldes.shacl;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdesClientStatusManager;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioPipelineManager;
import be.vlaanderen.informatievlaanderen.ldes.rdfrepo.Rdf4jRepositoryManager;
import be.vlaanderen.informatievlaanderen.ldes.rdfrepo.RepositoryValidator;
import be.vlaanderen.informatievlaanderen.ldes.valueobjects.ValidationReport;
import jakarta.annotation.PreDestroy;
import org.eclipse.rdf4j.model.Model;
import org.springframework.stereotype.Component;

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

	public ValidationReport validate(String ldesServerURl, Model shaclShape) {
		repositoryManager.createRepository();
		ldioPipelineManager.initPipeline(ldesServerURl);
		clientStatusManager.waitUntilReplicated();
		ldioPipelineManager.deletePipeline();
		final Model shaclValidationReport = validator.validate(shaclShape);
		repositoryManager.deleteRepository();
		return new ValidationReport(shaclValidationReport);
	}

	@PreDestroy
	public void onShutdown() {
		ldioPipelineManager.deletePipeline();
	}
}

package be.vlaanderen.informatievlaanderen.ldes.handlers;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdesClientStatusManager;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioManager;
import be.vlaanderen.informatievlaanderen.ldes.rdfrepo.RepositoryValidator;
import be.vlaanderen.informatievlaanderen.ldes.valueobjects.ValidationReport;
import jakarta.annotation.PreDestroy;
import org.eclipse.rdf4j.model.Model;
import org.springframework.stereotype.Component;

@Component
public class ShaclValidationHandler {
	private final LdioManager ldioManager;
	private final LdesClientStatusManager clientStatusManager;
	private final RepositoryValidator validator;

	public ShaclValidationHandler(LdioManager ldioManager, LdesClientStatusManager clientStatusManager, RepositoryValidator validator) {
		this.ldioManager = ldioManager;
		this.clientStatusManager = clientStatusManager;
		this.validator = validator;
	}

	public ValidationReport validate(String ldesServerURl, Model shaclShape) {
		ldioManager.initPipeline(ldesServerURl);
		clientStatusManager.waitUntilReplicated();
		ldioManager.deletePipeline();
		return new ValidationReport(validator.validate(shaclShape));

	}

	@PreDestroy
	public void onShutdown() {
		ldioManager.deletePipeline();
	}
}

package be.vlaanderen.informatievlaanderen.ldes.handlers;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioManager;
import be.vlaanderen.informatievlaanderen.ldes.rdfrepo.RepositoryValidator;
import be.vlaanderen.informatievlaanderen.ldes.valueobjects.ValidationReport;
import org.eclipse.rdf4j.model.Model;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;

@Component
public class ShaclValidationHandler {
	private final LdioManager ldioManager;
	private final RepositoryValidator validator;

	public ShaclValidationHandler(LdioManager ldioManager, RepositoryValidator validator) {
		this.ldioManager = ldioManager;
		this.validator = validator;
	}

	public ValidationReport validate(String url, Model shaclShape) {
		try {
			ldioManager.initPipeline(url);
			return new ValidationReport(validator.validate(shaclShape));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}

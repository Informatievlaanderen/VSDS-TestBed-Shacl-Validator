package be.vlaanderen.informatievlaanderen.ldes.gitb.rdfrepo;

import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.requests.PostRequest;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.config.LdioConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.gitb.shacl.valueobjects.ValidationReport;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ValidationParameters;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;

@Component
public class RepositoryValidator {
	private static final RDFFormat CONTENT_TYPE = RDFFormat.TURTLE;
	private static final Logger log = LoggerFactory.getLogger(RepositoryValidator.class);
	private final RequestExecutor requestExecutor;
	private final String repositoryValidationUrlTemplate;

	public RepositoryValidator(RequestExecutor requestExecutor, LdioConfigProperties ldioProperties) {
		this.requestExecutor = requestExecutor;
		this.repositoryValidationUrlTemplate = ldioProperties.getRepositoryValidationUrlTemplate();
	}

	public ValidationReport validate(ValidationParameters validationParameters) {
		return validate(validationParameters.pipelineName(), validationParameters.shaclShape());
	}

	public ValidationReport validate(String repositoryId, Model shaclShape) {
		log.atInfo().log("Validating repository ...");
		final StringWriter shaclShapeWriter = new StringWriter();
		Rio.write(shaclShape, shaclShapeWriter, CONTENT_TYPE);
		final String repositoryValidationUrl = repositoryValidationUrlTemplate.formatted(repositoryId);
		final PostRequest postRequest = new PostRequest(repositoryValidationUrl, shaclShapeWriter.toString(), CONTENT_TYPE.getDefaultMIMEType());
		final StringReader content = requestExecutor
				.execute(postRequest)
				.getBody()
				.map(StringReader::new)
				.orElseThrow(() -> new IllegalStateException("Unable to read validation report from response: missing report"));
		try {
			return new ValidationReport(Rio.parse(content, CONTENT_TYPE));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}

package be.vlaanderen.informatievlaanderen.ldes.rdfrepo;

import be.vlaanderen.informatievlaanderen.ldes.http.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.http.requests.PostRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioConfigProperties;
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

	public Model validate(String repositoryId, Model shaclShape) {
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
			return Rio.parse(content, CONTENT_TYPE);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}

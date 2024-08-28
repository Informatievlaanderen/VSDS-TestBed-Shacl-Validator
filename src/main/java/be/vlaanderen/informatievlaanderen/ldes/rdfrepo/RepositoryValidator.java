package be.vlaanderen.informatievlaanderen.ldes.rdfrepo;

import be.vlaanderen.informatievlaanderen.ldes.http.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.http.requests.PostRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioConfigProperties;
import org.apache.http.HttpEntity;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;

@Component
public class RepositoryValidator {
	private static final RDFFormat CONTENT_TYPE = RDFFormat.TURTLE;
	private static final Logger log = LoggerFactory.getLogger(RepositoryValidator.class);
	private final RequestExecutor requestExecutor;
	private final String repositoryValidationUrl;

	public RepositoryValidator(RequestExecutor requestExecutor, LdioConfigProperties ldioProperties) {
		this.requestExecutor = requestExecutor;
		this.repositoryValidationUrl = ldioProperties.getRepositoryValidationUrl();
	}

	public Model validate(Model shaclShape) {
		log.atInfo().log("Validating repository ...");
		final StringWriter shaclShapeWriter = new StringWriter();
		Rio.write(shaclShape, shaclShapeWriter, CONTENT_TYPE);
		final PostRequest postRequest = new PostRequest(repositoryValidationUrl, shaclShapeWriter.toString(), CONTENT_TYPE.getDefaultMIMEType());
		final HttpEntity response = requestExecutor.execute(postRequest);
		try {
			return Rio.parse(response.getContent(), CONTENT_TYPE);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}

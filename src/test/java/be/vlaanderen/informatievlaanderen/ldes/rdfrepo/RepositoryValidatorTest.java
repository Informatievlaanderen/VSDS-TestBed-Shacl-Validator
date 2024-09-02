package be.vlaanderen.informatievlaanderen.ldes.rdfrepo;

import be.vlaanderen.informatievlaanderen.ldes.gitb.rdfrepo.RepositoryValidator;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.HttpResponse;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.config.LdioConfigProperties;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RepositoryValidatorTest {
	private static final RDFFormat RDF_FORMAT = RDFFormat.TURTLE;
	private static final String SHACL_CONFORMS_URI = "http://www.w3.org/ns/shacl#conforms";
	private static final String REPOSITORY_ID = "validation-uuid";
	private static Model shaclShape;
	private RequestExecutor requestExecutor;
	private RepositoryValidator repoValidator;

	@BeforeAll
	static void beforeAll() {
		try {
			shaclShape = Rio.parse(new FileInputStream("src/test/resources/test-shape.ttl"), RDF_FORMAT);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@BeforeEach
	void setUp() {
		requestExecutor = mock();
		final LdioConfigProperties ldioConfigProperties = new LdioConfigProperties();
		ldioConfigProperties.setHost("http://localhost:8080");
		ldioConfigProperties.setSparqlHost("http://localhost:7200");
		repoValidator = new RepositoryValidator(requestExecutor, ldioConfigProperties);
	}

	@Test
	void given_ValidRepo_when_Validate_then_ReturnEmptyModel() throws IOException {
		final String validationReport = Files.readString(ResourceUtils.getFile("classpath:validation-report/valid.ttl").toPath());
		when(requestExecutor.execute(any())).thenReturn(new HttpResponse(200, validationReport));

		final Model result = repoValidator.validate(REPOSITORY_ID, shaclShape);

		assertThat(result)
				.filteredOn(statement -> statement.getPredicate().toString().equals(SHACL_CONFORMS_URI))
				.hasSize(1)
				.map(statement -> ((Literal) statement.getObject()).booleanValue())
				.first(InstanceOfAssertFactories.BOOLEAN)
				.isTrue();
	}

	@Test
	void given_InvalidRepo_when_Validate_then_ReturnNonEmptyModel() throws IOException {
		final String validationReport = Files.readString(ResourceUtils.getFile("classpath:validation-report/invalid.ttl").toPath());
		when(requestExecutor.execute(any())).thenReturn(new HttpResponse(200, validationReport));

		final Model result = repoValidator.validate(REPOSITORY_ID, shaclShape);

		assertThat(result)
				.filteredOn(statement -> statement.getPredicate().toString().equals(SHACL_CONFORMS_URI))
				.hasSize(1)
				.map(statement -> ((Literal) statement.getObject()).booleanValue())
				.first(InstanceOfAssertFactories.BOOLEAN)
				.isFalse();
	}
}
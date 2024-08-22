package be.vlaanderen.informatievlaanderen.ldes.rdfrepo;

import be.vlaanderen.informatievlaanderen.ldes.http.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioConfigProperties;
import org.apache.http.entity.InputStreamEntity;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RepositoryValidatorTest {
	private static final RDFFormat RDF_FORMAT = RDFFormat.TURTLE;
	private static final String SHACL_CONFORMS_URI = "http://www.w3.org/ns/shacl#conforms";
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
	void given_ValidRepo_when_Validate_then_ReturnEmptyModel() throws FileNotFoundException, URISyntaxException {
		final URI resource = Objects.requireNonNull(this.getClass().getClassLoader().getResource("validation-report/valid.ttl")).toURI();
		when(requestExecutor.execute(any())).thenReturn(new InputStreamEntity(new FileInputStream(new File(resource))));

		final Model result = repoValidator.validate(shaclShape);

		assertThat(result)
				.filteredOn(statement -> statement.getPredicate().toString().equals(SHACL_CONFORMS_URI))
				.hasSize(1)
				.map(statement -> ((Literal) statement.getObject()).booleanValue())
				.first(InstanceOfAssertFactories.BOOLEAN)
				.isTrue();
	}

	@Test
	void given_InvalidRepo_when_Validate_then_ReturnNonEmptyModel() throws FileNotFoundException, URISyntaxException {
		final URI resource = Objects.requireNonNull(this.getClass().getClassLoader().getResource("validation-report/invalid.ttl")).toURI();
		when(requestExecutor.execute(any())).thenReturn(new InputStreamEntity(new FileInputStream(new File(resource))));

		final Model result = repoValidator.validate(shaclShape);

		assertThat(result)
				.filteredOn(statement -> statement.getPredicate().toString().equals(SHACL_CONFORMS_URI))
				.hasSize(1)
				.map(statement -> ((Literal) statement.getObject()).booleanValue())
				.first(InstanceOfAssertFactories.BOOLEAN)
				.isFalse();
	}
}
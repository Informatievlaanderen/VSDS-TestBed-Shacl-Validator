package be.vlaanderen.informatievlaanderen.ldes.ldes.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.http.Request;
import be.vlaanderen.informatievlaanderen.ldes.http.RequestExecutor;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

@Service
public class EventStreamFetcher {
	public static final String LDES_VERSION_OF = "https://w3id.org/ldes#versionOfPath";

	final RequestExecutor requestExecutor;

	public EventStreamFetcher(RequestExecutor requestExecutor) {
		this.requestExecutor = requestExecutor;
	}

	public EventStreamProperties fetchProperties(String url) {
		final RDFFormat rdfFormat = RDFFormat.TURTLE;
		final HttpEntity response = requestExecutor.execute(new Request(url, RequestMethod.GET, ContentType.create(rdfFormat.getDefaultMIMEType())));
		final Model model = extractModel(response, rdfFormat);

		final Spliterator<Statement> statements = model.getStatements(null, SimpleValueFactory.getInstance().createIRI(LDES_VERSION_OF), null).spliterator();
		return StreamSupport.stream(statements, false)
				.findFirst()
				.map(statement -> new EventStreamProperties(extractCollectionName(statement.getSubject()), statement.getObject().stringValue()))
				.orElseThrow(() -> new IllegalStateException("Required properties of event stream for %s could not be found".formatted(url)));
	}

	private Model extractModel(HttpEntity response, RDFFormat rdfFormat) {
		try {
			return Rio.parse(response.getContent(), rdfFormat);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private String extractCollectionName(Resource resource) {
		if(resource.isIRI()) {
			IRI iri = (IRI) resource;
			return iri.getLocalName();
		}
		throw new IllegalStateException("Resource is not an IRI: " + resource);
	}
}

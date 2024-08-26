package be.vlaanderen.informatievlaanderen.ldes.ldes.services;

import be.vlaanderen.informatievlaanderen.ldes.http.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.http.requests.GetRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldes.valueobjects.EventStreamProperties;
import org.apache.http.HttpEntity;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.springframework.stereotype.Service;

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
		final HttpEntity response = requestExecutor.execute(new GetRequest(url));
		final Model model = extractModel(response, rdfFormat);

		final Spliterator<Statement> statements = model.getStatements(null, SimpleValueFactory.getInstance().createIRI(LDES_VERSION_OF), null).spliterator();
		return StreamSupport.stream(statements, false)
				.findFirst()
				.map(statement -> new EventStreamProperties(url, statement.getObject().stringValue()))
				.orElseThrow(() -> new IllegalStateException("Required properties of event stream for %s could not be found".formatted(url)));
	}

	private Model extractModel(HttpEntity response, RDFFormat rdfFormat) {
		try {
			return Rio.parse(response.getContent(), rdfFormat);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}

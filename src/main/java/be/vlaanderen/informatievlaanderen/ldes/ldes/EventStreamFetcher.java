package be.vlaanderen.informatievlaanderen.ldes.ldes;

import be.vlaanderen.informatievlaanderen.ldes.http.HttpResponse;
import be.vlaanderen.informatievlaanderen.ldes.http.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.http.requests.GetRequest;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
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
		final HttpResponse response = requestExecutor.execute(new GetRequest(url));
		final Model model = response.getBody()
				.map(content -> extractModel(content, rdfFormat))
				.orElseThrow(() -> new IllegalStateException("No eventstream properties could be extracted from %s".formatted(url)));

		final Spliterator<Statement> statements = model.getStatements(null, SimpleValueFactory.getInstance().createIRI(LDES_VERSION_OF), null).spliterator();
		return StreamSupport.stream(statements, false)
				.findFirst()
				.map(statement -> new EventStreamProperties(url, statement.getObject().stringValue()))
				.orElseThrow(() -> new IllegalStateException("Required properties of event stream for %s could not be found".formatted(url)));
	}

	private Model extractModel(String content, RDFFormat rdfFormat) {
		try {
			return Rio.parse(new StringReader(content), rdfFormat);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}

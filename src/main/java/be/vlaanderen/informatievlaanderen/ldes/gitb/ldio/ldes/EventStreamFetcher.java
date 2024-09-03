package be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.ldes;

import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.HttpResponse;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.requests.GetRequest;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.util.Iterator;

@Service
public class EventStreamFetcher {
	private static final String LDES_URI = "https://w3id.org/ldes#";
	private static final IRI LDES_VERSION_OF_PATH_IRI = SimpleValueFactory.getInstance().createIRI(LDES_URI, "versionOfPath");
	private static final IRI LDES_TIMESTAMP_PATH_IRI = SimpleValueFactory.getInstance().createIRI(LDES_URI, "timestampPath");

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

		final Statement versionOfStmt = extractProperty(url, LDES_VERSION_OF_PATH_IRI, model);
		final Statement timestampPath = extractProperty(url, LDES_TIMESTAMP_PATH_IRI, model);

		return new EventStreamProperties(url, versionOfStmt.getObject().stringValue(), timestampPath.getObject().stringValue());
	}

	private static Statement extractProperty(String url, IRI iri, Model model) {
		final Iterator<Statement> versionOfStatement = model
				.getStatements(null, iri, null)
				.iterator();
		if(!versionOfStatement.hasNext()) {
			throw new IllegalStateException("Required property %s could be extracted from %s".formatted(iri.toString(), url));
		}
		return versionOfStatement.next();
	}

	private Model extractModel(String content, RDFFormat rdfFormat) {
		try {
			return Rio.parse(new StringReader(content), rdfFormat);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

}

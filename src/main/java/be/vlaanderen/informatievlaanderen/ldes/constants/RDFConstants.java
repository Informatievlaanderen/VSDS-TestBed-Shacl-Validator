package be.vlaanderen.informatievlaanderen.ldes.constants;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class RDFConstants {
    public static final String SHACL = "http://www.w3.org/ns/shacl#";
    public static final IRI SEVERITY = SimpleValueFactory.getInstance().createIRI(SHACL + "resultSeverity");
    public static final IRI VIOLATION = SimpleValueFactory.getInstance().createIRI(SHACL + "Violation");
    public static final IRI WARNING = SimpleValueFactory.getInstance().createIRI(SHACL + "Warning");
    public static final IRI INFO = SimpleValueFactory.getInstance().createIRI(SHACL + "Info");
    private RDFConstants() {
    }
}

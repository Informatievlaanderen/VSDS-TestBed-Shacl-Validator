package be.vlaanderen.informatievlaanderen.ldes.constants;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class RDFConstants {
    public static String SHACL = "http://www.w3.org/ns/shacl#";
    public static IRI SEVERITY = SimpleValueFactory.getInstance().createIRI(SHACL + "resultSeverity");
    public static IRI VIOLATION = SimpleValueFactory.getInstance().createIRI(SHACL + "Violation");
    public static IRI WARNING = SimpleValueFactory.getInstance().createIRI(SHACL + "Warning");
    public static IRI INFO = SimpleValueFactory.getInstance().createIRI(SHACL + "Info");
    private RDFConstants() {
    }
}

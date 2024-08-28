package be.vlaanderen.informatievlaanderen.ldes.valueobjects.severitylevels;

import com.gitb.tr.ObjectFactory;
import com.gitb.tr.TAR;
import com.gitb.tr.TestAssertionReportType;
import jakarta.xml.bind.JAXBElement;
import org.eclipse.rdf4j.model.IRI;

public interface SeverityLevel {
	ObjectFactory objectMapper = new ObjectFactory();
	IRI getIri();

	JAXBElement<TestAssertionReportType> mapToJaxbElement(TestAssertionReportType testAssertionReportType);

	TAR createTarReport();
}

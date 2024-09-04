package be.vlaanderen.informatievlaanderen.ldes.gitb.shacl.valueobjects.severitylevels;

import be.vlaanderen.informatievlaanderen.ldes.gitb.services.suppliers.TarSupplier;
import com.gitb.tr.TAR;
import com.gitb.tr.TestAssertionReportType;
import jakarta.xml.bind.JAXBElement;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import static be.vlaanderen.informatievlaanderen.ldes.gitb.shacl.valueobjects.severitylevels.SeverityLevels.SHACL;

public class ErrorSeverityLevel implements SeverityLevel {
	private final IRI iri;

	ErrorSeverityLevel() {
		iri = SimpleValueFactory.getInstance().createIRI(SHACL, "Violation");
	}

	@Override
	public IRI getIri() {
		return iri;
	}

	@Override
	public JAXBElement<TestAssertionReportType> mapToJaxbElement(TestAssertionReportType testAssertionReportType) {
		return SeverityLevel.objectMapper.createTestAssertionGroupReportsTypeError(testAssertionReportType);
	}

	@Override
	public TAR createTarReport() {
		return TarSupplier.failure();
	}
}

package be.vlaanderen.informatievlaanderen.ldes.gitb.shacl.valueobjects.severitylevels;

import be.vlaanderen.informatievlaanderen.ldes.gitb.services.suppliers.TarSupplier;
import com.gitb.tr.TAR;
import com.gitb.tr.TestAssertionReportType;
import jakarta.xml.bind.JAXBElement;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import static be.vlaanderen.informatievlaanderen.ldes.gitb.shacl.valueobjects.severitylevels.SeverityLevels.SHACL;

public class WarningSeverityLevel implements SeverityLevel {
	private final IRI iri;

	WarningSeverityLevel() {
		iri = SimpleValueFactory.getInstance().createIRI(SHACL + "Warning");
	}

	@Override
	public IRI getIri() {
		return iri;
	}

	@Override
	public JAXBElement<TestAssertionReportType> mapToJaxbElement(TestAssertionReportType testAssertionReportType) {
		return SeverityLevel.objectMapper.createTestAssertionGroupReportsTypeWarning(testAssertionReportType);
	}

	@Override
	public TAR createTarReport() {
		return TarSupplier.warning();
	}
}

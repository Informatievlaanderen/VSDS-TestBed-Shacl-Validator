package be.vlaanderen.informatievlaanderen.ldes.gitb.shacl.valueobjects.severitylevels;

import be.vlaanderen.informatievlaanderen.ldes.gitb.services.suppliers.TarSupplier;
import com.gitb.tr.TAR;
import com.gitb.tr.TestAssertionReportType;
import jakarta.xml.bind.JAXBElement;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import static be.vlaanderen.informatievlaanderen.ldes.gitb.shacl.valueobjects.severitylevels.SeverityLevels.SHACL;

public class InfoSeverityLevel implements SeverityLevel {
	private final IRI iri;

	InfoSeverityLevel() {
		iri = SimpleValueFactory.getInstance().createIRI(SHACL + "Info");
	}

	@Override
	public IRI getIri() {
		return iri;
	}

	@Override
	public JAXBElement<TestAssertionReportType> mapToJaxbElement(TestAssertionReportType testAssertionReportType) {
		return SeverityLevel.objectMapper.createTestAssertionGroupReportsTypeInfo(testAssertionReportType);
	}

	@Override
	public TAR createTarReport() {
		return TarSupplier.success();
	}
}

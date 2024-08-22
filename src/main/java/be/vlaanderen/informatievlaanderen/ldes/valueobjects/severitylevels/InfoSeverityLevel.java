package be.vlaanderen.informatievlaanderen.ldes.valueobjects.severitylevels;

import be.vlaanderen.informatievlaanderen.ldes.constants.RDFConstants;
import be.vlaanderen.informatievlaanderen.ldes.services.TarSupplier;
import com.gitb.tr.TAR;
import com.gitb.tr.TestAssertionReportType;
import jakarta.xml.bind.JAXBElement;
import org.eclipse.rdf4j.model.IRI;

public class InfoSeverityLevel implements SeverityLevel {
	InfoSeverityLevel() {
	}

	@Override
	public IRI getIri() {
		return RDFConstants.INFO;
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

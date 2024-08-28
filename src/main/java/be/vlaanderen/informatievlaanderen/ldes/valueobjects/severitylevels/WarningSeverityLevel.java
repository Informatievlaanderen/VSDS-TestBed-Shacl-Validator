package be.vlaanderen.informatievlaanderen.ldes.valueobjects.severitylevels;

import be.vlaanderen.informatievlaanderen.ldes.constants.RDFConstants;
import be.vlaanderen.informatievlaanderen.ldes.services.TarSupplier;
import com.gitb.tr.TAR;
import com.gitb.tr.TestAssertionReportType;
import com.gitb.tr.TestResultType;
import jakarta.xml.bind.JAXBElement;
import org.eclipse.rdf4j.model.IRI;

public class WarningSeverityLevel implements SeverityLevel {
	WarningSeverityLevel() {

	}

	@Override
	public IRI getIri() {
		return RDFConstants.WARNING;
	}

	@Override
	public JAXBElement<TestAssertionReportType> mapToJaxbElement(TestAssertionReportType testAssertionReportType) {
		return SeverityLevel.objectMapper.createTestAssertionGroupReportsTypeWarning(testAssertionReportType);
	}

	@Override
	public TAR createTarReport() {
		return new TarSupplier(TestResultType.WARNING).get();
	}
}

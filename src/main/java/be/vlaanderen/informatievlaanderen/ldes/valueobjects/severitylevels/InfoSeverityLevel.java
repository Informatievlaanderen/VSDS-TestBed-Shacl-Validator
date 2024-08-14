package be.vlaanderen.informatievlaanderen.ldes.valueobjects.severitylevels;

import com.gitb.tr.TAR;
import com.gitb.tr.TestAssertionReportType;
import jakarta.xml.bind.JAXBElement;
import org.eclipse.rdf4j.model.IRI;

import static be.vlaanderen.informatievlaanderen.ldes.constants.RDFConstants.INFO;

public class InfoSeverityLevel implements SeverityLevel {
	@Override
	public IRI getIri() {
		return INFO;
	}

	@Override
	public JAXBElement<TestAssertionReportType> mapToJaxbElement(TestAssertionReportType testAssertionReportType) {
		return SeverityLevel.objectMapper.createTestAssertionGroupReportsTypeInfo(testAssertionReportType);
	}

	@Override
	public void setResult(TAR report) {
		// No result should be set on info severity level
	}
}

package be.vlaanderen.informatievlaanderen.ldes.valueobjects.severitylevels;

import com.gitb.tr.TAR;
import com.gitb.tr.TestAssertionReportType;
import com.gitb.tr.TestResultType;
import jakarta.xml.bind.JAXBElement;
import org.eclipse.rdf4j.model.IRI;

import static be.vlaanderen.informatievlaanderen.ldes.constants.RDFConstants.WARNING;

public class WarningSeverityLevel implements SeverityLevel {
	@Override
	public IRI getIri() {
		return WARNING;
	}

	@Override
	public JAXBElement<TestAssertionReportType> mapToJaxbElement(TestAssertionReportType testAssertionReportType) {
		return SeverityLevel.objectMapper.createTestAssertionGroupReportsTypeWarning(testAssertionReportType);
	}

	@Override
	public void setResult(TAR report) {
		report.setResult(TestResultType.WARNING);
	}
}

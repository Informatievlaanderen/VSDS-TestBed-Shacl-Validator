package be.vlaanderen.informatievlaanderen.ldes.valueobjects.severitylevels;

import be.vlaanderen.informatievlaanderen.ldes.constants.RDFConstants;
import com.gitb.tr.TAR;
import com.gitb.tr.TestAssertionGroupReportsType;
import com.gitb.tr.TestAssertionReportType;
import com.gitb.tr.TestResultType;
import jakarta.xml.bind.JAXBElement;
import org.eclipse.rdf4j.model.IRI;

import static be.vlaanderen.informatievlaanderen.ldes.constants.RDFConstants.VIOLATION;

public class ErrorSeverityLevel implements SeverityLevel {
	@Override
	public IRI getIri() {
		return VIOLATION;
	}

	@Override
	public JAXBElement<TestAssertionReportType> mapToJaxbElement(TestAssertionReportType testAssertionReportType) {
		return SeverityLevel.objectMapper.createTestAssertionGroupReportsTypeError(testAssertionReportType);
	}

	@Override
	public void setResult(TAR report) {
		report.setResult(TestResultType.FAILURE);
	}
}
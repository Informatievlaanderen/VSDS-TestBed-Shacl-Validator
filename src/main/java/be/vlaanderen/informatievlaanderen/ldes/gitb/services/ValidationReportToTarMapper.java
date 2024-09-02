package be.vlaanderen.informatievlaanderen.ldes.gitb.services;

import be.vlaanderen.informatievlaanderen.ldes.gitb.shacl.valueobjects.ValidationReport;
import be.vlaanderen.informatievlaanderen.ldes.gitb.shacl.valueobjects.severitylevels.SeverityLevel;
import com.gitb.tr.BAR;
import com.gitb.tr.TAR;
import com.gitb.tr.TestAssertionGroupReportsType;
import com.gitb.tr.ValidationCounters;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;

import java.math.BigInteger;

public class ValidationReportToTarMapper {
	private ValidationReportToTarMapper() {
	}

	public static TAR mapToTar(ValidationReport validationReport) {
		final SeverityLevel highestSeverityLevel = validationReport.getHighestSeverityLevel();
		final TAR tarReport = highestSeverityLevel.createTarReport();

		final TestAssertionGroupReportsType reportsType = new TestAssertionGroupReportsType();
		reportsType.getInfoOrWarningOrError().add(highestSeverityLevel
				.mapToJaxbElement(createReportItemContent(validationReport.shaclReport())));
		tarReport.setReports(reportsType);
		tarReport.setCounters(extractValidationCounters(validationReport));
		return tarReport;
	}

	private static BAR createReportItemContent(Model shaclReport) {
		BAR itemContent = new BAR();
		itemContent.setDescription(RDFConverter.writeModel(shaclReport, RDFFormat.TURTLE));
		return itemContent;
	}

	private static ValidationCounters extractValidationCounters(ValidationReport validationReport) {
		final ValidationCounters validationCounters = new ValidationCounters();
		validationCounters.setNrOfAssertions(BigInteger.valueOf(validationReport.infoCount()));
		validationCounters.setNrOfWarnings(BigInteger.valueOf(validationReport.warningCount()));
		validationCounters.setNrOfErrors(BigInteger.valueOf(validationReport.errorCount()));
		return new ValidationCounters();

	}
}

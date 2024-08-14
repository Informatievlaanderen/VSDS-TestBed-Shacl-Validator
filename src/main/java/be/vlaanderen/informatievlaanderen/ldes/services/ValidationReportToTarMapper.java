package be.vlaanderen.informatievlaanderen.ldes.services;

import be.vlaanderen.informatievlaanderen.ldes.gitb.Utils;
import be.vlaanderen.informatievlaanderen.ldes.valueobjects.ValidationReport;
import be.vlaanderen.informatievlaanderen.ldes.valueobjects.severitylevels.SeverityLevel;
import com.gitb.core.AnyContent;
import com.gitb.tr.*;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.math.BigInteger;
import java.util.GregorianCalendar;

public class ValidationReportToTarMapper {
	private ValidationReportToTarMapper() {

	}

	public static TAR mapToTar(ValidationReport validationReport) {
		final TAR tarReport = initTar();
		final SeverityLevel highestSeverityLevel = validationReport.getHighestSeverityLevel();
		highestSeverityLevel.setResult(tarReport);

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

	private static TAR initTar() {
		TAR report = new TAR();
		report.setContext(new AnyContent());
		report.getContext().setType("map");
		report.setResult(TestResultType.SUCCESS);
		try {
			report.setDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
		} catch (DatatypeConfigurationException e) {
			throw new IllegalStateException(e);
		}
		return report;
	}

	private static ValidationCounters extractValidationCounters(ValidationReport validationReport) {
		final ValidationCounters validationCounters = new ValidationCounters();
		validationCounters.setNrOfAssertions(BigInteger.valueOf(validationReport.infoCount()));
		validationCounters.setNrOfWarnings(BigInteger.valueOf(validationReport.warningCount()));
		validationCounters.setNrOfErrors(BigInteger.valueOf(validationReport.errorCount()));
		return new ValidationCounters();

	}
}

package be.vlaanderen.informatievlaanderen.ldes.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.valueobjects.severitylevels.ErrorSeverityLevel;
import be.vlaanderen.informatievlaanderen.ldes.valueobjects.severitylevels.InfoSeverityLevel;
import be.vlaanderen.informatievlaanderen.ldes.valueobjects.severitylevels.SeverityLevel;
import be.vlaanderen.informatievlaanderen.ldes.valueobjects.severitylevels.WarningSeverityLevel;
import com.gitb.tr.ValidationCounters;
import com.google.common.collect.Iterables;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;

import java.math.BigInteger;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.constants.RDFConstants.*;

public record ValidationReport(Model shaclReport) {

	public int errorCount() {
		return getCountFor(VIOLATION);
	}

	public int warningCount() {
		return getCountFor(WARNING);
	}

	public int infoCount() {
		return getCountFor(INFO);
	}

	public SeverityLevel getHighestSeverityLevel() {
		return Map.of(new ErrorSeverityLevel(), errorCount(),
						new WarningSeverityLevel(), warningCount(),
						new InfoSeverityLevel(), infoCount())
				.entrySet().stream()
				.filter(entry -> entry.getValue() > 0)
				.findFirst()
				.map(Map.Entry::getKey)
				.orElse(new InfoSeverityLevel());
	}

	private int getCountFor(IRI severity) {
		return Iterables.size(shaclReport.getStatements(null, SEVERITY, severity));
	}
}

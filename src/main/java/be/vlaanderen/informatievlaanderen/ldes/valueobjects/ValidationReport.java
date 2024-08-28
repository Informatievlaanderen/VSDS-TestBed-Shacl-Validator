package be.vlaanderen.informatievlaanderen.ldes.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.valueobjects.severitylevels.SeverityLevel;
import be.vlaanderen.informatievlaanderen.ldes.valueobjects.severitylevels.SeverityLevels;
import com.google.common.collect.Iterables;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
		return Arrays.stream(SeverityLevels.all())
				.collect(Collectors.toMap(Function.identity(), severityLevel -> getCountFor(severityLevel.getIri())))
				.entrySet().stream()
				.filter(entry -> entry.getValue() > 0)
				.findFirst()
				.map(Map.Entry::getKey)
				.orElse(SeverityLevels.INFO);
	}

	private int getCountFor(IRI severity) {
		return Iterables.size(shaclReport.getStatements(null, SEVERITY, severity));
	}
}

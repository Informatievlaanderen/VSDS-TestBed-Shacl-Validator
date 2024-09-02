package be.vlaanderen.informatievlaanderen.ldes.gitb.shacl.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.gitb.shacl.valueobjects.severitylevels.SeverityLevel;
import be.vlaanderen.informatievlaanderen.ldes.gitb.shacl.valueobjects.severitylevels.SeverityLevels;
import com.google.common.collect.Iterables;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import java.util.Arrays;
import java.util.Map;

import static be.vlaanderen.informatievlaanderen.ldes.gitb.shacl.valueobjects.severitylevels.SeverityLevels.SHACL;


public record ValidationReport(Model shaclReport) {
	private static final IRI SEVERITY = SimpleValueFactory.getInstance().createIRI(SHACL, "resultSeverity");

	public int errorCount() {
		return getCountFor(SeverityLevels.ERROR.getIri());
	}

	public int warningCount() {
		return getCountFor(SeverityLevels.WARNING.getIri());
	}

	public int infoCount() {
		return getCountFor(SeverityLevels.INFO.getIri());
	}

	public SeverityLevel getHighestSeverityLevel() {
		return Arrays.stream(SeverityLevels.all())
				.map(severityLevel -> Map.entry(severityLevel, getCountFor(severityLevel.getIri())))
				.filter(entry -> entry.getValue() > 0)
				.findFirst()
				.map(Map.Entry::getKey)
				.orElse(SeverityLevels.INFO);
	}

	private int getCountFor(IRI severity) {
		return Iterables.size(shaclReport.getStatements(null, SEVERITY, severity));
	}

	@Override
	public String toString() {
		return "ValidationReport [errorCount=" + errorCount() + ", warningCount=" + warningCount() + ", infoCount=" + infoCount() + "]";
	}
}

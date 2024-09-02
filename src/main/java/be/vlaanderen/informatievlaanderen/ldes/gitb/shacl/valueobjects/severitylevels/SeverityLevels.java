package be.vlaanderen.informatievlaanderen.ldes.gitb.shacl.valueobjects.severitylevels;

public class SeverityLevels {
	public static final String SHACL = "http://www.w3.org/ns/shacl#";

	private SeverityLevels() {}

	public static final SeverityLevel ERROR = new ErrorSeverityLevel();
	public static final SeverityLevel WARNING = new WarningSeverityLevel();
	public static final SeverityLevel INFO = new InfoSeverityLevel();

	public static SeverityLevel[] all() {
		return new SeverityLevel[] {
				ERROR,
				WARNING,
				INFO
		};
	}
}

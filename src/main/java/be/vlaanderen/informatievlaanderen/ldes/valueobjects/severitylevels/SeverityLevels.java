package be.vlaanderen.informatievlaanderen.ldes.valueobjects.severitylevels;

public class SeverityLevels {
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

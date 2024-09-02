package be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.excpeptions;

public class LdesClientStatusUnavailableException extends RuntimeException {
	@Override
	public String getMessage() {
		return "Ldes client status not available.";
	}
}

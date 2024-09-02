package be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.valuebojects;

public enum ClientStatus {

	REPLICATING,
	SYNCHRONISING,
	COMPLETED,
	ERROR;

	public static boolean isSuccessfullyReplicated(ClientStatus status) {
		return SYNCHRONISING.equals(status) || ClientStatus.COMPLETED.equals(status);
	}

}

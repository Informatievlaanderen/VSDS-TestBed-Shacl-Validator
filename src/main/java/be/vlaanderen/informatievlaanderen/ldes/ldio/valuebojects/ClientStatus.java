package be.vlaanderen.informatievlaanderen.ldes.ldio.valuebojects;

public enum ClientStatus {

	REPLICATING,
	SYNCHRONISING,
	COMPLETED,
	ERROR;

	public static boolean isSuccessfullyReplicated(ClientStatus status) {
		return status.equals(ClientStatus.SYNCHRONISING) || status.equals(ClientStatus.COMPLETED);
	}

}

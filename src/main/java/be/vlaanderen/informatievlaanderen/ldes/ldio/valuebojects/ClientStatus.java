package be.vlaanderen.informatievlaanderen.ldes.ldio.valuebojects;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ClientStatus(@JsonProperty("pipeline") String pipelineName, Status status) {
	public boolean isSuccessfullyReplicated() {
		return status.equals(Status.SYNCHRONIZING) || status.equals(Status.COMPLETED);
	}

	public boolean isReplicating() {
		return status.equals(Status.REPLICATING);
	}

	public enum Status {
		REPLICATING,
		SYNCHRONIZING,
		COMPLETED,
		ERROR
	}
}

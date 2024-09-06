package be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects;

import java.util.Objects;
import java.util.UUID;

public class SessionId {
	public static final String PIPELINE_NAME_TEMPLATE = "validation-pipeline-%s";

	private final String uuid;

	private SessionId(String uuid) {
		this.uuid = uuid;
	}

	public String getId() {
		return uuid;
	}

	public String getPipelineName() {
		return PIPELINE_NAME_TEMPLATE.formatted(uuid);
	}

	public static SessionId from(String uuid) {
		return new SessionId(uuid == null ? UUID.randomUUID().toString() : uuid);
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SessionId sessionId)) return false;

		return Objects.equals(uuid, sessionId.uuid);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(uuid);
	}

	@Override
	public String toString() {
		return uuid;
	}
}

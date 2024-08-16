package be.vlaanderen.informatievlaanderen.ldes.ldes.valueobjects;

public record EventStreamProperties(
		String ldesServerUrl,
		String collectionName,
		String versionOfPath
) {
}

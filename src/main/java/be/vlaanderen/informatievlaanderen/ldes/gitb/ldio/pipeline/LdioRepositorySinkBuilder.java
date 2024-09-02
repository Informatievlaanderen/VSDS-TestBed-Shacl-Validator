package be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.pipeline;

import java.util.HashMap;

public class LdioRepositorySinkBuilder extends LdioComponentBuilder {

	protected LdioRepositorySinkBuilder() {
		super("Ldio:RepositorySink", new HashMap<>());
	}

	public LdioRepositorySinkBuilder withSparqlHost(String sparqlHost) {
		setProperty("sparql-host", sparqlHost);
		return this;
	}

	public LdioRepositorySinkBuilder withRepositoryId(String repositoryId) {
		setProperty("repository-id", repositoryId);
		return this;
	}

	public LdioRepositorySinkBuilder withBatchSize(int batchSize) {
		setProperty("batch-size", batchSize);
		return this;
	}
}

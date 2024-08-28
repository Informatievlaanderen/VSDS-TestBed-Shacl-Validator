package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline;

import java.util.HashMap;

public class LdioRepositorySinkBuilder extends LdioComponentBuilder<LdioRepositorySinkBuilder>{

	protected LdioRepositorySinkBuilder() {
		super("Ldio:RepositorySink", new HashMap<>());
	}

	public LdioRepositorySinkBuilder withSparqlHost(String sparqlHost) {
		return withProperty("sparql-host", sparqlHost);
	}

	public LdioRepositorySinkBuilder withRepositoryId(String repositoryId) {
		return withProperty("repository-id", repositoryId);
	}

	public LdioRepositorySinkBuilder withBatchSize(int batchSize) {
		return withProperty("batch-size", batchSize);
	}
}

package be.vlaanderen.informatievlaanderen.ldes.rdfrepo;

import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioConfigProperties;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.config.RepositoryImplConfig;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.sail.config.SailRepositoryConfig;
import org.eclipse.rdf4j.sail.memory.config.MemoryStoreConfig;
import org.eclipse.rdf4j.sail.shacl.config.ShaclSailConfig;
import org.springframework.stereotype.Component;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioConfigProperties.REPOSITORY_ID;

@Component
public class Rdf4jRepositoryManager {
	private final RepositoryManager repositoryManager;

	public Rdf4jRepositoryManager(LdioConfigProperties ldioProperties) {
		repositoryManager = new RemoteRepositoryManager(ldioProperties.getSparqlHost());
		repositoryManager.init();
	}

	public void initRepository() {
		final RepositoryImplConfig repositoryTypeSpec = new SailRepositoryConfig(new ShaclSailConfig(new MemoryStoreConfig(true)));
		final RepositoryConfig config = new RepositoryConfig(REPOSITORY_ID, repositoryTypeSpec);
		repositoryManager.addRepositoryConfig(config);
	}
}

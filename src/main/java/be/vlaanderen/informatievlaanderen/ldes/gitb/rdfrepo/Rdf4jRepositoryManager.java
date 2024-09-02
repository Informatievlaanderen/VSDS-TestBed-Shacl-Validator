package be.vlaanderen.informatievlaanderen.ldes.gitb.rdfrepo;

import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.config.LdioConfigProperties;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.config.RepositoryImplConfig;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.sail.config.SailRepositoryConfig;
import org.eclipse.rdf4j.sail.memory.config.MemoryStoreConfig;
import org.eclipse.rdf4j.sail.shacl.config.ShaclSailConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Rdf4jRepositoryManager {
	private static final Logger log = LoggerFactory.getLogger(Rdf4jRepositoryManager.class);
	private final RepositoryManager repositoryManager;

	public Rdf4jRepositoryManager(LdioConfigProperties ldioProperties) {
		repositoryManager = new RemoteRepositoryManager(ldioProperties.getSparqlHost());
		repositoryManager.init();
	}

	public void createRepository(String repositoryId) {
		final RepositoryImplConfig repositoryTypeSpec = new SailRepositoryConfig(new ShaclSailConfig(new MemoryStoreConfig(true)));
		final RepositoryConfig config = new RepositoryConfig(repositoryId, repositoryTypeSpec);
		repositoryManager.addRepositoryConfig(config);
		log.atInfo().log("Repository created with repository id: {}", repositoryId);
	}

	public void deleteRepository(String repositoryId) {
		repositoryManager.removeRepository(repositoryId);
		log.atInfo().log("Repository deleted with repository id: {}", repositoryId);
	}
}

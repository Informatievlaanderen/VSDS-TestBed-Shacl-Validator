package be.vlaanderen.informatievlaanderen.ldes.rdfrepo;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.http.config.HTTPRepositoryConfig;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryProvider;
import org.springframework.stereotype.Component;

@Component
public class Rdf4jRepositoryManager {

    private static final String repoIdBase = "validation";
    private final String serverUrl = "http://localhost:8080/rdf4j-server";
    private final RepositoryManager repositoryManager;

    public Rdf4jRepositoryManager() {
        repositoryManager = RepositoryProvider.getRepositoryManager(serverUrl);
        repositoryManager.init();
    }

    public String initRepo() {
        String repoId = repositoryManager.getNewRepositoryID(repoIdBase);
        new RepositoryConfig(repoId, new HTTPRepositoryConfig(serverUrl));
        repositoryManager.addRepositoryConfig(new RepositoryConfig());
        return repoId;
    }

    public Repository getRepo(String id) {
        return repositoryManager.getRepository(id);
    }
}

package be.vlaanderen.informatievlaanderen.ldes.rdfrepo;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.http.config.HTTPRepositoryConfig;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.ValidationPipelineSupplier.REPOSITORY_ID;

@Component
public class Rdf4jRepositoryManager {
    private final String repoServerUrl;
    private final RepositoryManager repositoryManager;

    public Rdf4jRepositoryManager(@Value("${ldio.sparql-host}") String repoServerUrl) {
	    this.repoServerUrl = repoServerUrl + "/rdf4j-server";
	    repositoryManager = RepositoryProvider.getRepositoryManager(repoServerUrl);
        repositoryManager.init();
    }

    public String initRepo() {
        String repoId = repositoryManager.getNewRepositoryID(REPOSITORY_ID);
        final RepositoryConfig repoConfig = new RepositoryConfig(repoId, new HTTPRepositoryConfig(repoServerUrl));
        repositoryManager.addRepositoryConfig(repoConfig);
        return repoId;
    }

    public Repository getRepo(String id) {
        return repositoryManager.getRepository(id);
    }
}

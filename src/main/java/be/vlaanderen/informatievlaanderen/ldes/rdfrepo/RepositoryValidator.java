package be.vlaanderen.informatievlaanderen.ldes.rdfrepo;

import be.vlaanderen.informatievlaanderen.ldes.http.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.http.requests.PostRequest;
import be.vlaanderen.informatievlaanderen.ldes.services.RDFConverter;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.stereotype.Component;

@Component
public class RepositoryValidator {

    private static final String REPO_VALIDATION_URL_TEMPLATE = "%s/rest/repositories/%s/validate/text";
    private static final RDFFormat CONTENT_TYPE = RDFFormat.TURTLE;
    private String repoUrl;
    private final Rdf4jRepositoryManager repositoryManager;
    private final RequestExecutor requestExecutor;
    private RepositoryConnection connection;

    public RepositoryValidator(Rdf4jRepositoryManager repositoryManager, RequestExecutor requestExecutor) {
        this.repositoryManager = repositoryManager;
        this.requestExecutor = requestExecutor;
    }

    public Model validate(Model shaclShape) {
        String repositoryId = repositoryManager.initRepo();
        Repository repository = repositoryManager.getRepo(repositoryId);
        try {
            Model validationReport = new LinkedHashModel();
            requestExecutor.execute(new PostRequest(
                    String.format(REPO_VALIDATION_URL_TEMPLATE, repoUrl, repositoryId),
                    RDFConverter.writeModel(shaclShape, CONTENT_TYPE),
                    CONTENT_TYPE.getName()));

//            RepositoryConnection connection = repository.getConnection();
//            connection.begin();
//            connection.getStatements(null, null, null, );

            return validationReport;
        } finally {
            repository.shutDown();
        }

    }
}

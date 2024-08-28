package be.vlaanderen.informatievlaanderen.ldes.handlers;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioManager;
import be.vlaanderen.informatievlaanderen.ldes.rdfrepo.RepositoryValidator;
import org.eclipse.rdf4j.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ShaclValidationHandler {
    @Autowired
    private LdioManager ldioManager;
    @Autowired
    private RepositoryValidator validator;

    public Model validate(String url, Model shaclShape) throws IOException {
        ldioManager.initPipeline(url);
        return validator.validate(shaclShape);
    }

}

package be.vlaanderen.informatievlaanderen.ldes.handlers;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioManager;
import be.vlaanderen.informatievlaanderen.ldes.rdfrepo.RepositoryValidator;
import org.eclipse.rdf4j.model.Model;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ShaclValidationHandler {
    private final LdioManager ldioManager;
    private final RepositoryValidator validator;

    public ShaclValidationHandler(LdioManager ldioManager, RepositoryValidator validator) {
        this.ldioManager = ldioManager;
        this.validator = validator;
    }

    public Model validate(String url, Model shaclShape) throws IOException {
        ldioManager.initPipeline(url);
        return validator.validate(shaclShape);
    }

}

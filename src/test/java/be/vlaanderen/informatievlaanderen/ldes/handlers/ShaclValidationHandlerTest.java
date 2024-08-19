package be.vlaanderen.informatievlaanderen.ldes.handlers;

import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@SpringBootTest(properties = {"ldio.host=http://localhost:8383", "ldio.sparql-host=http://host.docker.internal:7200"})
@ComponentScan(value = { "be.vlaanderen.informatievlaanderen.ldes" })
class ShaclValidationHandlerTest {
    @Autowired
    ShaclValidationHandler validationHandler;

    @Test
    void test() {
        validationHandler.validate("http://host.docker.internal:8082/verkeersmetingen", new LinkedHashModel());
        System.out.println("Finished validation");
    }

}
package be.vlaanderen.informatievlaanderen.ldes.handlers;

import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@SpringBootTest
@ComponentScan(value = { "be.vlaanderen.informatievlaanderen.ldes" })
class ShaclValidationHandlerTest {
    @Autowired
    ShaclValidationHandler validationHandler;

    @Test
    void test() {
        validationHandler.validate("http://localhost:8082", new LinkedHashModel());
    }

}
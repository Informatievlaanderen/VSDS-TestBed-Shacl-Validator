package be.vlaanderen.informatievlaanderen.ldes.handlers;

import org.eclipse.rdf4j.model.impl.DynamicModelFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@EnableAutoConfiguration
@SpringBootTest
@ComponentScan(value = { "be.vlaanderen.informatievlaanderen.ldes.server" })
class ShaclValidationHandlerTest {
    @Autowired
    ShaclValidationHandler validationHandler;

    @Test
    void test() throws IOException {
        validationHandler.validate("http://localhost:8082", new DynamicModelFactory().createEmptyModel());
    }

}
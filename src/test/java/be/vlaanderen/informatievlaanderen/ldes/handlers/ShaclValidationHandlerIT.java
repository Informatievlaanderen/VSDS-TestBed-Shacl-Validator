package be.vlaanderen.informatievlaanderen.ldes.handlers;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.io.FileInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@EnableAutoConfiguration
@SpringBootTest(properties = {"ldio.host=http://localhost:8383", "ldio.sparql-host=http://CI00321761:7200"})
@ComponentScan(value = { "be.vlaanderen.informatievlaanderen.ldes" })
class ShaclValidationHandlerIT {
    @Autowired
    ShaclValidationHandler validationHandler;

    @Test
    void run() throws IOException {
        final Model shaclShape = Rio.parse(new FileInputStream("src/test/resources/test-shape.ttl"), RDFFormat.TURTLE);
        final var report = validationHandler.validate("http://CI00321761:8082/verkeersmetingen", shaclShape);
        assertThat(report.shaclReport()).isNotEmpty();
    }

}
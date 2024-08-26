package be.vlaanderen.informatievlaanderen.ldes.handlers;

import be.vlaanderen.informatievlaanderen.ldes.gitb.ServiceConfig;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ValidationServiceImpl;
import be.vlaanderen.informatievlaanderen.ldes.http.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.rdfrepo.Rdf4jRepositoryManager;
import com.gitb.vs.ValidationService;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@EnableAutoConfiguration
@SpringBootTest(properties = {"ldio.host=http://ldio-workbench:8080", "ldio.sparql-host=http://graph-db:7200"}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = ServiceConfig.class)
@ComponentScan(value = {"be.vlaanderen.informatievlaanderen.ldes"})
class ShaclValidationHandlerIT {
	@MockBean
	private RequestExecutor requestExecutor;
	@MockBean
	private Rdf4jRepositoryManager rdf4jRepositoryManager;
	@Autowired
	private ShaclValidationHandler validationHandler;
	@Autowired
	private TestRestTemplate restTemplate;
	@LocalServerPort
	private int port;


	@Test
	void test() throws IOException {
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_XML);
		String requestPayload = Files.readString(ResourceUtils.getFile("classpath:validate-request.xml").toPath());

		// TODO: mock the whole process
		// 1. fetch the event stream props
		// 2. post a pipeline
		// 3. fetch the client status (multiple times maybe? => first a 404 status, then a replicating, then a synchronising)
		// 4. delete request (is only a verification)
		// 5. post shacl shape
		// 6. verify all calls (including the not stubbed methods like all the RDF4JRepoManagerCalls) => IN ORDER!!!

		restTemplate.postForEntity("/services/validation?wsdl", new HttpEntity<>(requestPayload, headers), String.class);
	}

}
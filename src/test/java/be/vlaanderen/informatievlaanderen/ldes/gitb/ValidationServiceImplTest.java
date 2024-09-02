package be.vlaanderen.informatievlaanderen.ldes.gitb;

import be.vlaanderen.informatievlaanderen.ldes.gitb.config.ServiceConfig;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.HttpResponse;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.requests.DeleteRequest;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.requests.GetRequest;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.requests.PostRequest;
import be.vlaanderen.informatievlaanderen.ldes.gitb.rdfrepo.Rdf4jRepositoryManager;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.gitb.shacl.valueobjects.ValidationParameters.PIPELINE_NAME_TEMPLATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@EnableAutoConfiguration
@SpringBootTest(properties = {"ldio.host=http://ldio-workbench:8080", "ldio.sparql-host=http://graph-db:7200"}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = ServiceConfig.class)
@ComponentScan(value = {"be.vlaanderen.informatievlaanderen.ldes"})
class ValidationServiceImplTest {
	private static final String LDIO_HOST = "http://ldio-workbench:8080";
	private static final String PIPELINE_UUID = "test-pipeline-uuid";
	private static final String LDIO_LDES_CLIENT_STATUS_URL = LDIO_HOST + "/admin/api/v1/pipeline/ldes-client/validation-pipeline-" + PIPELINE_UUID;
	private static final String LDES_SERVER_URL = "http://ldes-server:8080/verkeersmetingen";
	@MockBean
	private RequestExecutor requestExecutor;
	@MockBean
	private Rdf4jRepositoryManager rdf4jRepositoryManager;
	@Autowired
	private TestRestTemplate restTemplate;

	static Stream<Arguments> provideShaclShapes() {
		return Stream.of(
				Arguments.of("validation-report/invalid.ttl", "sh:conforms false;"),
				Arguments.of("validation-report/valid.ttl", "sh:conforms true;")
		);
	}

	@ParameterizedTest
	@MethodSource("provideShaclShapes")
	void test_ValidationServiceImpl(String fileName, String expectedShaclConformity) throws IOException {
		when(requestExecutor.execute(new GetRequest(LDES_SERVER_URL)))
				.thenReturn(createResponse(ResourceUtils.getFile("classpath:event-stream.ttl")));
		when(requestExecutor.execute(any(PostRequest.class), eq(201)))
				.thenReturn(new HttpResponse(201, null));
		when(requestExecutor.execute(new GetRequest(LDIO_LDES_CLIENT_STATUS_URL), 200, 404))
				.thenReturn(new HttpResponse(404, null))
				.thenReturn(new HttpResponse(200, "\"REPLICATING\""))
				.thenReturn(new HttpResponse(200, "\"SYNCHRONISING\""));
		when(requestExecutor.execute(any(DeleteRequest.class), eq(202), eq(204)))
				.thenReturn(new HttpResponse(202, null));
		when(requestExecutor.execute(any(PostRequest.class)))
				.thenReturn(createResponse(ResourceUtils.getFile("classpath:" + fileName)));

		final var result = restTemplate.postForEntity("/services/validation?wsdl", createRequest(), String.class);

		assertThat(result)
				.extracting(HttpEntity::getBody, InstanceOfAssertFactories.STRING)
				.contains(expectedShaclConformity);
		verify(rdf4jRepositoryManager).createRepository(PIPELINE_NAME_TEMPLATE.formatted(PIPELINE_UUID));
		verify(rdf4jRepositoryManager).deleteRepository(PIPELINE_NAME_TEMPLATE.formatted(PIPELINE_UUID));
		verify(requestExecutor).execute(any(PostRequest.class), eq(201));
		verify(requestExecutor).execute(any(DeleteRequest.class), eq(202), eq(204));
	}

	private static HttpResponse createResponse(File file) throws IOException {
		return new HttpResponse(200, Files.readString(file.toPath()));
	}

	private static HttpEntity<String> createRequest() throws IOException {
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_XML);
		final String requestPayload = Files.readString(ResourceUtils.getFile("classpath:validate-request.xml").toPath());
		return new HttpEntity<>(requestPayload, headers);
	}
}
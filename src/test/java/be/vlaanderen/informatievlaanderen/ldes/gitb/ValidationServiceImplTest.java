package be.vlaanderen.informatievlaanderen.ldes.gitb;

import be.vlaanderen.informatievlaanderen.ldes.http.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.http.requests.DeleteRequest;
import be.vlaanderen.informatievlaanderen.ldes.http.requests.GetRequest;
import be.vlaanderen.informatievlaanderen.ldes.http.requests.PostRequest;
import be.vlaanderen.informatievlaanderen.ldes.rdfrepo.Rdf4jRepositoryManager;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.InputStreamEntity;
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

import static be.vlaanderen.informatievlaanderen.ldes.valueobjects.ValidationParameters.PIPELINE_NAME_TEMPLATE;
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
				.thenReturn(new BasicHttpEntity());
		when(requestExecutor.execute(new GetRequest(LDIO_LDES_CLIENT_STATUS_URL), 200, 404))
				.thenReturn(createEmptyResponse())
				.thenReturn(createResponse("\"REPLICATING\""))
				.thenReturn(createResponse("\"SYNCHRONISING\""));
		when(requestExecutor.execute(any(DeleteRequest.class), eq(202), eq(204)))
				.thenReturn(new BasicHttpEntity());
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

	private static BasicHttpEntity createEmptyResponse() {
		final BasicHttpEntity response = new BasicHttpEntity();
		response.setContentLength(0);
		return response;
	}

	private static BasicHttpEntity createResponse(String content) {
		final BasicHttpEntity response = new BasicHttpEntity();
		response.setContent(new ByteArrayInputStream(content.getBytes()));
		response.setContentLength(content.length());
		return response;
	}

	private static InputStreamEntity createResponse(File file) throws IOException {
		return new InputStreamEntity(new FileInputStream(file));
	}

	private static HttpEntity<String> createRequest() throws IOException {
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_XML);
		final String requestPayload = Files.readString(ResourceUtils.getFile("classpath:validate-request.xml").toPath());
		return new HttpEntity<>(requestPayload, headers);
	}
}
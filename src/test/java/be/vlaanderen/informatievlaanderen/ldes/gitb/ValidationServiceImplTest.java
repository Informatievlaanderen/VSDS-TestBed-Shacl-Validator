package be.vlaanderen.informatievlaanderen.ldes.gitb;

import be.vlaanderen.informatievlaanderen.ldes.gitb.config.ServiceConfig;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.HttpResponse;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.requests.PostRequest;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@EnableAutoConfiguration
@SpringBootTest(properties = {"ldio.host=http://ldio-workbench:8080", "ldio.sparql-host=http://graph-db:7200"}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = ServiceConfig.class)
@ComponentScan(value = {"be.vlaanderen.informatievlaanderen.ldes"})
class ValidationServiceImplTest {
	@MockBean
	private RequestExecutor requestExecutor;
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
		when(requestExecutor.execute(any(PostRequest.class)))
				.thenReturn(createResponse(ResourceUtils.getFile("classpath:" + fileName)));

		final var result = restTemplate.postForEntity("/services/validation?wsdl", createRequest(), String.class);

		assertThat(result)
				.extracting(HttpEntity::getBody, InstanceOfAssertFactories.STRING)
				.contains(expectedShaclConformity);
		verify(requestExecutor).execute(any(PostRequest.class));
	}

	private static HttpResponse createResponse(File file) throws IOException {
		return new HttpResponse(200, Files.readString(file.toPath()));
	}

	private static HttpEntity<String> createRequest() throws IOException {
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_XML);
		final String requestPayload = Files.readString(ResourceUtils.getFile("classpath:soap-requests/validate-request.xml").toPath());
		return new HttpEntity<>(requestPayload, headers);
	}
}
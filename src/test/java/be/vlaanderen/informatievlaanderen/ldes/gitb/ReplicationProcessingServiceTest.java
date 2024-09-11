package be.vlaanderen.informatievlaanderen.ldes.gitb;

import be.vlaanderen.informatievlaanderen.ldes.gitb.config.ServiceConfig;
import be.vlaanderen.informatievlaanderen.ldes.gitb.services.replication.DestroyPipelineProcessExecutor;
import be.vlaanderen.informatievlaanderen.ldes.gitb.services.replication.HaltWhenReplicatedProcessExecutor;
import be.vlaanderen.informatievlaanderen.ldes.gitb.services.replication.ProcessExecutor;
import be.vlaanderen.informatievlaanderen.ldes.gitb.services.replication.StartReplicatingProcessExecutor;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.SessionId;
import org.junit.jupiter.api.Test;
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

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@EnableAutoConfiguration
@SpringBootTest(properties = {"ldio.host=http://ldio-workbench:8080", "ldio.sparql-host=http://graph-db:7200"}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = ServiceConfig.class)
@ComponentScan(value = {"be.vlaanderen.informatievlaanderen.ldes"})
class ReplicationProcessingServiceTest {
	private static final String TEST_PIPELINE_UUID = "test-session-uuid";
	private static final SessionId TEST_PIPELINE_SESSION_ID = SessionId.from(TEST_PIPELINE_UUID);
	@MockBean(name = StartReplicatingProcessExecutor.NAME)
	private ProcessExecutor startReplicatingProcessExecutor;
	@MockBean(name = HaltWhenReplicatedProcessExecutor.NAME)
	private ProcessExecutor checkReplicatingProcessExecutor;
	@MockBean(name = DestroyPipelineProcessExecutor.NAME)
	private ProcessExecutor destroyPipelineProcessExecutor;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void test_StartReplicating() throws IOException {
		final String content = """
				<operation>startReplicating</operation>
				<input name="ldes-url" embeddingMethod="STRING">
				    <v11:value>http://ldes-server:8080/verkeersmetingen</v11:value>
				</input>
				""";
		restTemplate.postForEntity("/services/process?wsdl", createRequest(content), String.class);

		verify(startReplicatingProcessExecutor).execute(assertArg(params ->
				assertThat(params)
						.extracting("sessionId")
						.isEqualTo(TEST_PIPELINE_SESSION_ID)
		));
	}

	@Test
	void test_HaltWhenReplicated() throws IOException {
		final String content = "<operation>haltWhenReplicated</operation>";
		restTemplate.postForEntity("/services/process?wsdl", createRequest(content), String.class);

		verify(checkReplicatingProcessExecutor).execute(assertArg(params -> assertThat(params)
				.extracting("sessionId")
				.isEqualTo(TEST_PIPELINE_SESSION_ID)
		));
	}

	@Test
	void test_DestroyPipeline() throws IOException {
		final String content = "<operation>destroyPipeline</operation>";
		restTemplate.postForEntity("/services/process?wsdl", createRequest(content), String.class);

		verify(destroyPipelineProcessExecutor).execute(assertArg(params -> assertThat(params)
				.extracting("sessionId")
				.isEqualTo(TEST_PIPELINE_SESSION_ID)
		));
	}

	@Test
	void test_InvalidStartReplicatingRequest() throws IOException {
		final String content = """
				<operation>start-replicating</operation>
				<input name="ldes-url" embeddingMethod="STRING">
				    <v11:value>http://ldes-server:8080/verkeersmetingen</v11:value>
				</input>
				""";
		final HttpEntity<String> request = createRequest(content);

		restTemplate.postForEntity("/services/process?wsdl", request, String.class);

		verifyNoInteractions(startReplicatingProcessExecutor, checkReplicatingProcessExecutor);
	}

	private static HttpEntity<String> createRequest(String content) throws IOException {
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_XML);
		final String requestPayload = readPayload(content);
		return new HttpEntity<>(requestPayload, headers);
	}

	private static String readPayload(String content) throws IOException {
		final String payLoadTemplate = Files.readString(ResourceUtils.getFile("classpath:soap-requests/process-request.xml").toPath());
		return payLoadTemplate.replace("<!--BLANK_SPACE-->", content);
	}


}
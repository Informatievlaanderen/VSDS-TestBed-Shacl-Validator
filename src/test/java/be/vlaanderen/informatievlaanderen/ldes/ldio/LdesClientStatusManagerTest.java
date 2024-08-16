package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.http.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valuebojects.ClientStatus;
import org.apache.http.HttpEntity;
import org.apache.http.entity.BasicHttpEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.time.Duration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.ValidationPipelineFactory.PIPELINE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LdesClientStatusManagerTest {
	@Mock
	private RequestExecutor requestExecutor;
	private LdesClientStatusManager ldesClientStatusManager;

	@BeforeEach
	void setUp() {
		final LdioConfigProperties ldioConfigProperties = new LdioConfigProperties();
		ldioConfigProperties.setHost("http://ldio-workben-host.vlaanderen.be");
		ldesClientStatusManager = new LdesClientStatusManager(requestExecutor, ldioConfigProperties);
	}

	@Test
	void test_WaitUntilReplicated() {
		when(requestExecutor.execute(any()))
				.thenReturn(createResponse(ClientStatus.Status.REPLICATING))
				.thenReturn(createResponse(ClientStatus.Status.REPLICATING))
				.thenReturn(createResponse(ClientStatus.Status.SYNCHRONIZING));

		ldesClientStatusManager.waitUntilReplicated();

		await()
				.atMost(Duration.ofSeconds(10))
				.untilAsserted(() -> verify(requestExecutor, times(3)).execute(any()));

	}

	@ParameterizedTest
	@EnumSource(ClientStatus.Status.class)
	void test_GetClientStatus(ClientStatus.Status status) {
		final ClientStatus expectedClientStatus = new ClientStatus(PIPELINE_NAME, status);
		when(requestExecutor.execute(any())).thenReturn(createResponse(status));

		final ClientStatus actualStatus = ldesClientStatusManager.getClientStatus();

		assertThat(expectedClientStatus).isEqualTo(actualStatus);
	}

	private HttpEntity createResponse(ClientStatus.Status status) {
		final BasicHttpEntity response = new BasicHttpEntity();
		response.setContent(new ByteArrayInputStream(createJson(status).getBytes()));
		return response;
	}

	private String createJson(ClientStatus.Status status) {
		return """
				{
				"pipeline": "%s",
				"status": "%s"
				}
				""".formatted(PIPELINE_NAME, status);
	}
}
package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.http.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.http.requests.GetRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.excpeptions.LdesClientStatusUnavailableException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valuebojects.ClientStatus;
import org.apache.http.HttpEntity;
import org.apache.http.entity.BasicHttpEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LdesClientStatusManagerTest {
	private static final String PIPELINE_NAME = "test-pipeline";
	private final Integer[] expectedStatusCodes = {200, 404};
	@Mock
	private RequestExecutor requestExecutor;
	private LdesClientStatusManager ldesClientStatusManager;
	private ThreadPoolTaskScheduler scheduler;

	@BeforeEach
	void setUp() {
		final LdioConfigProperties ldioConfigProperties = new LdioConfigProperties();
		ldioConfigProperties.setHost("http://ldio-workben-host.vlaanderen.be");

		scheduler = new ThreadPoolTaskScheduler();
		scheduler.initialize();

		ldesClientStatusManager = new LdesClientStatusManager(requestExecutor, ldioConfigProperties, scheduler);
	}

	@AfterEach
	void tearDown() {
		if (scheduler != null) {
			scheduler.shutdown();
		}
	}

	@Test
	void test_WaitUntilReplicated() {
		when(requestExecutor.execute(any(), eq(expectedStatusCodes)))
				.thenReturn(createResponse(ClientStatus.REPLICATING))
				.thenReturn(createResponse(ClientStatus.REPLICATING))
				.thenReturn(createResponse(ClientStatus.SYNCHRONISING));

		ldesClientStatusManager.waitUntilReplicated(PIPELINE_NAME);

		verify(requestExecutor, timeout(10000).times(3)).execute(any(), eq(expectedStatusCodes));
	}

	@Test
	void test_WaitUntilReplicated_when_StatusUnavailable() {
		final BasicHttpEntity response = new BasicHttpEntity();
		response.setContentLength(0);
		when(requestExecutor.execute(any(GetRequest.class), eq(200), eq(404))).thenReturn(response);

		assertThatThrownBy(() -> ldesClientStatusManager.waitUntilReplicated(PIPELINE_NAME))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("Unable to fetch the LDES client status");

		verify(requestExecutor, times(5)).execute(any(GetRequest.class), eq(200), eq(404));
	}

	@Test
	void when_ClientStatusCannotBeFound_then_ThrowException() {
		final BasicHttpEntity response = new BasicHttpEntity();
		response.setContentLength(0);
		when(requestExecutor.execute(any(), eq(expectedStatusCodes))).thenReturn(response);

		assertThatThrownBy(() -> ldesClientStatusManager.getClientStatus(PIPELINE_NAME))
				.isInstanceOf(LdesClientStatusUnavailableException.class)
				.hasMessage("Ldes client status not available.");
	}

	@ParameterizedTest
	@EnumSource(ClientStatus.class)
	void test_GetClientStatus(ClientStatus status) {
		when(requestExecutor.execute(any(), eq(expectedStatusCodes))).thenReturn(createResponse(status));

		final ClientStatus actualStatus = ldesClientStatusManager.getClientStatus(PIPELINE_NAME);

		assertThat(actualStatus).isEqualTo(status);
	}

	private HttpEntity createResponse(ClientStatus status) {
		final BasicHttpEntity response = new BasicHttpEntity();
		response.setContent(new ByteArrayInputStream(('"' + status.toString() + '"').getBytes()));
		return response;
	}
}
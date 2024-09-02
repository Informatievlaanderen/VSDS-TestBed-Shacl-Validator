package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.LdesClientStatusManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.HttpResponse;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.requests.GetRequest;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.config.LdioConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.valuebojects.ClientStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LdesClientStatusManagerTest {
	private static final String PIPELINE_NAME = "test-pipeline";
	private final Integer[] expectedStatusCodes = {200, 404};
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
		when(requestExecutor.execute(any(), eq(expectedStatusCodes)))
				.thenReturn(createEmptyResponse())
				.thenReturn(createResponse(ClientStatus.REPLICATING))
				.thenReturn(createResponse(ClientStatus.REPLICATING))
				.thenReturn(createResponse(ClientStatus.SYNCHRONISING));

		ldesClientStatusManager.waitUntilReplicated(PIPELINE_NAME);

		verify(requestExecutor, timeout(15000).times(4)).execute(any(), eq(expectedStatusCodes));
	}

	@Test
	void test_WaitUntilReplicated_when_StatusUnavailable() {
		when(requestExecutor.execute(any(GetRequest.class), eq(200), eq(404))).thenReturn(createEmptyResponse());

		ldesClientStatusManager.waitUntilReplicated(PIPELINE_NAME);

		verify(requestExecutor, times(5)).execute(any(GetRequest.class), eq(200), eq(404));
	}

	private static HttpResponse createEmptyResponse() {
		return new HttpResponse(404, null);
	}

	private static HttpResponse createResponse(ClientStatus status) {
		final String content = '"' + status.toString() + '"';
		return new HttpResponse(200, content);
	}
}
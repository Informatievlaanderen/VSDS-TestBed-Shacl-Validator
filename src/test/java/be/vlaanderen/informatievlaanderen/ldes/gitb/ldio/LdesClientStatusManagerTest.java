package be.vlaanderen.informatievlaanderen.ldes.gitb.ldio;

import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.config.LdioConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.valuebojects.ClientStatus;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.HttpResponse;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.requests.GetRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
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


	@ParameterizedTest
	@EnumSource(ClientStatus.class)
	void test_GetClientStatus(ClientStatus status) {
		when(requestExecutor.execute(any(GetRequest.class), eq(expectedStatusCodes))).thenReturn(createResponse(status));

		final ClientStatus actual = ldesClientStatusManager.getClientStatus(PIPELINE_NAME);

		assertThat(actual).isEqualTo(status);
	}

	private static HttpResponse createEmptyResponse() {
		return new HttpResponse(404, null);
	}

	private static HttpResponse createResponse(ClientStatus status) {
		final String content = '"' + status.toString() + '"';
		return new HttpResponse(200, content);
	}
}
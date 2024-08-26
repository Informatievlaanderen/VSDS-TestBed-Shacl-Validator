package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.http.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.excpeptions.LdesClientStatusUnavailableException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LdesClientStatusManagerTest {
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
				.thenReturn(createResponse(ClientStatus.REPLICATING))
				.thenReturn(createResponse(ClientStatus.REPLICATING))
				.thenReturn(createResponse(ClientStatus.SYNCHRONISING));

		ldesClientStatusManager.waitUntilReplicated();

		verify(requestExecutor, timeout(10000)).execute(any(), eq(expectedStatusCodes));

	}

	@Test
	void when_ClientStatusCannotBeFound_then_ThrowException() {
		final BasicHttpEntity response = new BasicHttpEntity();
		response.setContentLength(0);
		when(requestExecutor.execute(any(), eq(expectedStatusCodes))).thenReturn(response);

		assertThatThrownBy(ldesClientStatusManager::getClientStatus)
				.isInstanceOf(LdesClientStatusUnavailableException.class)
				.hasMessage("Ldes client status not available.");
	}

	@ParameterizedTest
	@EnumSource(ClientStatus.class)
	void test_GetClientStatus(ClientStatus status) {
		when(requestExecutor.execute(any(), eq(expectedStatusCodes))).thenReturn(createResponse(status));

		final ClientStatus actualStatus = ldesClientStatusManager.getClientStatus();

		assertThat(actualStatus).isEqualTo(status);
	}

	private HttpEntity createResponse(ClientStatus status) {
		final BasicHttpEntity response = new BasicHttpEntity();
		response.setContent(new ByteArrayInputStream(('"' + status.toString() + '"').getBytes()));
		return response;
	}
}
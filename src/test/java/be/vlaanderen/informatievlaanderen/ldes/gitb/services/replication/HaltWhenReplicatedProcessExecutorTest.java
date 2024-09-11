package be.vlaanderen.informatievlaanderen.ldes.gitb.services.replication;

import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.LdesClientStatusManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.LdioPipelineManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.excpeptions.LdesClientStatusUnavailableException;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.valuebojects.ClientStatus;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.Message;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessParameters;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessResult;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.SessionId;
import com.gitb.tr.TestResultType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HaltWhenReplicatedProcessExecutorTest {
	private static final String SESSION_UUID = "my-test-session-uuid";
	private static final SessionId SESSION_ID = SessionId.from(SESSION_UUID);
	@Mock
	private LdesClientStatusManager ldesClientStatusManager;
	@Mock
	private LdioPipelineManager ldioPipelineManager;
	@InjectMocks
	private HaltWhenReplicatedProcessExecutor haltWhenReplicatedProcessExecutor;

	@Test
	void given_ReplicatingStatus_testExecute() {
		final ProcessResult expected = new ProcessResult(
				TestResultType.SUCCESS,
				new Message("STATUS", "REPLICATING"),
				new Message("MESSAGE", "CLIENT is not SYNCHRONISING or COMPLETED yet")
		);
		when(ldesClientStatusManager.getClientStatus(anyString())).thenReturn(ClientStatus.REPLICATING);

		final ProcessResult processResult = haltWhenReplicatedProcessExecutor.execute(new ProcessParameters(SESSION_UUID, List.of()));

		assertThat(processResult)
				.usingRecursiveComparison()
				.isEqualTo(expected);
		verifyNoInteractions(ldioPipelineManager);
	}

	@EnumSource(value = ClientStatus.class, names = {"COMPLETED", "SYNCHRONISING"}, mode = EnumSource.Mode.INCLUDE)
	@ParameterizedTest
	void given_ClientStatus_test_Execute(ClientStatus clientStatus) {
		final ProcessResult expected = new ProcessResult(
				TestResultType.SUCCESS,
				new Message("STATUS", clientStatus.name()),
				new Message("MESSAGE", "PIPELINE will be PAUSED soon")
		);
		when(ldesClientStatusManager.getClientStatus(anyString())).thenReturn(clientStatus);

		final ProcessResult processResult = haltWhenReplicatedProcessExecutor.execute(new ProcessParameters(SESSION_UUID, List.of()));

		assertThat(processResult)
				.usingRecursiveComparison()
				.isEqualTo(expected);
		verify(ldioPipelineManager).haltPipeline(SESSION_ID.getPipelineName());
	}

	@Test
	void given_ClientStatusUnavailable_test_Execute() {
		final ProcessResult expected = new ProcessResult(
				TestResultType.FAILURE,
				new Message("ERROR", "Ldes client status not available.")
		);
		when(ldesClientStatusManager.getClientStatus(anyString())).thenThrow(LdesClientStatusUnavailableException.class);

		final ProcessResult actual = haltWhenReplicatedProcessExecutor.execute(new ProcessParameters(SESSION_UUID, List.of()));

		assertThat(actual)
				.usingRecursiveComparison()
				.isEqualTo(expected);
		verifyNoInteractions(ldioPipelineManager);
	}
}
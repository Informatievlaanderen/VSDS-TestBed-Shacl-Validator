package be.vlaanderen.informatievlaanderen.ldes.gitb.services.replication;

import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.LdioPipelineManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.rdfrepo.Rdf4jRepositoryManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.Message;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessParameters;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessResult;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.SessionId;
import com.gitb.tr.TestResultType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class DestroyPipelineProcessExecutorTest {
	private static final String SESSION_UUID = "my-test-session-uuid";
	@Mock
	private LdioPipelineManager ldioPipelineManager;
	@Mock
	private Rdf4jRepositoryManager rdf4jRepositoryManager;
	@InjectMocks
	private DestroyPipelineProcessExecutor destroyPipelineProcessExecutor;

	@Test
	void test_Execute() {
		final String pipelineName = SessionId.from(SESSION_UUID).getPipelineName();
		final ProcessResult expected = new ProcessResult(TestResultType.SUCCESS, new Message("MESSAGE", "Pipeline '%s' is deleted".formatted(pipelineName)));
		final var actual = destroyPipelineProcessExecutor.execute(new ProcessParameters(SESSION_UUID, List.of()));

		verify(ldioPipelineManager).deletePipeline(pipelineName);
		verify(rdf4jRepositoryManager).deleteRepository(pipelineName);
		assertThat(actual)
				.usingRecursiveComparison()
				.isEqualTo(expected);
	}
}
package be.vlaanderen.informatievlaanderen.ldes.gitb.services.replication;

import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.LdioPipelineManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.rdfrepo.Rdf4jRepositoryManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.*;
import com.gitb.tr.TestResultType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessParameters.LDES_URL_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StartReplicatingProcessExecutorTest {
	private static final SessionId SESSION_ID = SessionId.from("my-test-session-uuid");
	private static final String LDES_URL = "http://ldes-server:8080/collection/view";
	private static final String PIPELINE_NAME = SESSION_ID.getPipelineName();
	@Mock
	private Rdf4jRepositoryManager repositoryManager;
	@Mock
	private LdioPipelineManager ldioPipelineManager;
	@InjectMocks
	private StartReplicatingProcessExecutor startReplicatingProcessExecutor;

	@Test
	void test_Process() {
		final ProcessResult expected = new ProcessResult(TestResultType.SUCCESS, Message.info("Pipeline 'validation-pipeline-my-test-session-uuid' created"));
		final Parameters parameters = mock();
		when(parameters.getStringForName(LDES_URL_KEY)).thenReturn(LDES_URL);

		final ProcessResult result = startReplicatingProcessExecutor.execute(new ProcessParameters(SESSION_ID, parameters));

		assertThat(result)
				.usingRecursiveComparison()
				.isEqualTo(expected);


		final InOrder inOrder = inOrder(repositoryManager, ldioPipelineManager);
		inOrder.verify(repositoryManager).createRepository(PIPELINE_NAME);
		inOrder.verify(ldioPipelineManager).initPipeline(LDES_URL, PIPELINE_NAME);
		inOrder.verifyNoMoreInteractions();
	}
}
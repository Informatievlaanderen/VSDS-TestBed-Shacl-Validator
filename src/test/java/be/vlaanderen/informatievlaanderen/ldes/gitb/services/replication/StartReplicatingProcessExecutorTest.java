package be.vlaanderen.informatievlaanderen.ldes.gitb.services.replication;

import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.LdioPipelineManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.rdfrepo.Rdf4jRepositoryManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.Parameters;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessParameters.LDES_URL_KEY;
import static be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessParameters.PIPELINE_NAME_TEMPLATE;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StartReplicatingProcessExecutorTest {
	private static final String SESSION_ID = "my-test-session-uuid";
	private static final String LDES_URL = "http://ldes-server:8080/collection/view";
	private static final String PIPELINE_NAME = PIPELINE_NAME_TEMPLATE.formatted(SESSION_ID);
	@Mock
	private Rdf4jRepositoryManager repositoryManager;
	@Mock
	private LdioPipelineManager ldioPipelineManager;
	@InjectMocks
	private StartReplicatingProcessExecutor startReplicatingProcessExecutor;

	@Test
	void test_Process() {
		final Parameters parameters = mock();
		when(parameters.getStringForName(LDES_URL_KEY)).thenReturn(LDES_URL);
		startReplicatingProcessExecutor.execute(new ProcessParameters(SESSION_ID, parameters));

		final InOrder inOrder = inOrder(repositoryManager, ldioPipelineManager);
		inOrder.verify(repositoryManager).createRepository(PIPELINE_NAME);
		inOrder.verify(ldioPipelineManager).initPipeline(LDES_URL, PIPELINE_NAME);
		inOrder.verifyNoMoreInteractions();
	}
}
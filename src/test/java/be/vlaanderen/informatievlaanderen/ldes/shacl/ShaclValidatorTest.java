package be.vlaanderen.informatievlaanderen.ldes.shacl;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdesClientStatusManager;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioPipelineManager;
import be.vlaanderen.informatievlaanderen.ldes.rdfrepo.Rdf4jRepositoryManager;
import be.vlaanderen.informatievlaanderen.ldes.rdfrepo.RepositoryValidator;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class ShaclValidatorTest {
	private static final String LDES_SERVER_URL = "http://ldes-server:8080/collection";
	@Mock
	private Rdf4jRepositoryManager repositoryManager;
	@Mock
	private LdioPipelineManager ldioPipelineManager;
	@Mock
	private LdesClientStatusManager ldesClientStatusManager;
	@Mock
	private RepositoryValidator repositoryValidator;

	@InjectMocks
	private ShaclValidator shaclValidator;

	@Test
	void test() {
		shaclValidator.validate(LDES_SERVER_URL, new LinkedHashModel());

		final InOrder inOrder = inOrder(ldioPipelineManager, ldesClientStatusManager, repositoryManager, repositoryValidator);
		inOrder.verify(repositoryManager).createRepository();
		inOrder.verify(ldioPipelineManager).initPipeline(LDES_SERVER_URL);
		inOrder.verify(ldesClientStatusManager).waitUntilReplicated();
		inOrder.verify(ldioPipelineManager).deletePipeline();
		inOrder.verify(repositoryValidator).validate(new LinkedHashModel());
		inOrder.verify(repositoryManager).deleteRepository();
		inOrder.verifyNoMoreInteractions();
	}
}
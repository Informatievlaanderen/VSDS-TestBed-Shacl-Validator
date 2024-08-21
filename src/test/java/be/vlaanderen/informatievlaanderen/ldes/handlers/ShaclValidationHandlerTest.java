package be.vlaanderen.informatievlaanderen.ldes.handlers;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdesClientStatusManager;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioManager;
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
class ShaclValidationHandlerTest {
	private static final String LDES_SERVER_URL = "http://ldes-server:8080/collection";
	@Mock
	private Rdf4jRepositoryManager repositoryManager;
	@Mock
	private LdioManager ldioManager;
	@Mock
	private LdesClientStatusManager ldesClientStatusManager;
	@Mock
	private RepositoryValidator repositoryValidator;

	@InjectMocks
	private ShaclValidationHandler shaclValidationHandler;

	@Test
	void test() {
		shaclValidationHandler.validate(LDES_SERVER_URL, new LinkedHashModel());

		final InOrder inOrder = inOrder(ldioManager, ldesClientStatusManager, repositoryManager, repositoryValidator);
		inOrder.verify(repositoryManager).initRepository();
		inOrder.verify(ldioManager).initPipeline(LDES_SERVER_URL);
		inOrder.verify(ldesClientStatusManager).waitUntilReplicated();
		inOrder.verify(ldioManager).deletePipeline();
		inOrder.verify(repositoryValidator).validate(new LinkedHashModel());
		inOrder.verifyNoMoreInteractions();
	}
}
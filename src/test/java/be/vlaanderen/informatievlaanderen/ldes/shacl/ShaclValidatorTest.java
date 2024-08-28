package be.vlaanderen.informatievlaanderen.ldes.shacl;

import be.vlaanderen.informatievlaanderen.ldes.ldio.LdesClientStatusManager;
import be.vlaanderen.informatievlaanderen.ldes.ldio.LdioPipelineManager;
import be.vlaanderen.informatievlaanderen.ldes.rdfrepo.Rdf4jRepositoryManager;
import be.vlaanderen.informatievlaanderen.ldes.rdfrepo.RepositoryValidator;
import be.vlaanderen.informatievlaanderen.ldes.valueobjects.ValidationParameters;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.ValidationPipelineSupplier.PIPELINE_NAME_TEMPLATE;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class ShaclValidatorTest {
	private static final String LDES_SERVER_URL = "http://ldes-server:8080/collection";
	private static final String PIPELINE_UUID = "test-pipeline-uuid";
	private static final String PIPELINE_NAME = PIPELINE_NAME_TEMPLATE.formatted(PIPELINE_UUID);
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
		shaclValidator.validate(new ValidationParameters(LDES_SERVER_URL, new LinkedHashModel(), PIPELINE_UUID));

		final InOrder inOrder = inOrder(ldioPipelineManager, ldesClientStatusManager, repositoryManager, repositoryValidator);
		inOrder.verify(repositoryManager).createRepository();
		inOrder.verify(ldioPipelineManager).initPipeline(LDES_SERVER_URL, PIPELINE_NAME);
		inOrder.verify(ldesClientStatusManager).waitUntilReplicated(PIPELINE_NAME);
		inOrder.verify(ldioPipelineManager).deletePipeline(PIPELINE_NAME);
		inOrder.verify(repositoryValidator).validate(new LinkedHashModel());
		inOrder.verify(repositoryManager).deleteRepository();
		inOrder.verifyNoMoreInteractions();
	}
}
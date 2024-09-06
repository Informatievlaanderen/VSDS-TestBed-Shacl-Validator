package be.vlaanderen.informatievlaanderen.ldes.gitb.shacl;

import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.LdesClientStatusManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.LdioPipelineManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.rdfrepo.Rdf4jRepositoryManager;
import be.vlaanderen.informatievlaanderen.ldes.gitb.rdfrepo.RepositoryValidator;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.Parameters;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.SessionId;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ValidationParameters;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ValidationParameters.PIPELINE_NAME_TEMPLATE;
import static be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ValidationParameters.SHACL_SHAPE_KEY;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShaclValidatorTest {
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
		Parameters params = mock();
		when(params.getStringForName(SHACL_SHAPE_KEY)).thenReturn("");
		shaclValidator.validate(new ValidationParameters(SessionId.from(PIPELINE_UUID), params));

		final InOrder inOrder = inOrder(ldioPipelineManager, ldesClientStatusManager, repositoryManager, repositoryValidator);
		inOrder.verify(ldesClientStatusManager).waitUntilReplicated(PIPELINE_NAME);
		inOrder.verify(ldioPipelineManager).haltPipeline(PIPELINE_NAME);
		inOrder.verify(repositoryValidator).validate(PIPELINE_NAME, new LinkedHashModel());
		inOrder.verify(repositoryManager).deleteRepository(anyString());
		inOrder.verify(ldioPipelineManager).deletePipeline(PIPELINE_NAME);
		inOrder.verifyNoMoreInteractions();
	}
}
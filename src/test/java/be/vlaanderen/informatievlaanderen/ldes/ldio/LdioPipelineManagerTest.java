package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.PostRequestAssert;
import be.vlaanderen.informatievlaanderen.ldes.http.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.http.requests.DeleteRequest;
import be.vlaanderen.informatievlaanderen.ldes.http.requests.PostRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldes.EventStreamFetcher;
import be.vlaanderen.informatievlaanderen.ldes.ldes.EventStreamProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioConfigProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.entity.ContentType;
import org.assertj.core.api.InstanceOfAssertFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ResourceUtils;

import java.io.IOException;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.ValidationPipelineSupplier.PIPELINE_NAME_TEMPLATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LdioPipelineManagerTest {
	private static final String LDIO_HOST = "http://localhost:8080";
	private static final String SPARQL_HOST = "http://my-sparql-host.net";
	private static final String LDES_SERVER_URL = "http://test-server/test-collection";
	private static final String PIPELINE_UUID = "test-pipeline-uuid";
	private static final String PIPELINE_NAME = PIPELINE_NAME_TEMPLATE.formatted(PIPELINE_UUID);
	@Mock
	private EventStreamFetcher eventStreamFetcher;
	@Mock
	private RequestExecutor requestExecutor;
	private LdioPipelineManager ldioPipelineManager;

	@BeforeEach
	void setUp() {
		final LdioConfigProperties ldioConfigProperties = new LdioConfigProperties();
		ldioConfigProperties.setHost(LDIO_HOST);
		ldioConfigProperties.setSparqlHost(SPARQL_HOST);
		ldioPipelineManager = new LdioPipelineManager(eventStreamFetcher, requestExecutor, ldioConfigProperties);
	}

	@Test
	void test_InitPipeline() throws IOException {
		final JsonNode expectedJson = new ObjectMapper().readTree(ResourceUtils.getFile("classpath:ldio-pipeline.json"));
		when(eventStreamFetcher.fetchProperties(LDES_SERVER_URL)).thenReturn(new EventStreamProperties(LDES_SERVER_URL, "http://purl.org/dc/terms/isVersionOf"));

		ldioPipelineManager.initPipeline(LDES_SERVER_URL, PIPELINE_NAME);

		verify(eventStreamFetcher).fetchProperties(LDES_SERVER_URL);
		verify(requestExecutor).execute(
				assertArg(actual -> assertThat(actual)
						.asInstanceOf(new InstanceOfAssertFactory<>(PostRequest.class, PostRequestAssert::new))
						.hasUrl(LDIO_HOST + "/admin/api/v1/pipeline")
						.hasBody(expectedJson)
						.hasContentType(ContentType.APPLICATION_JSON)),
				eq(201));
	}

	@Test
	void test_DeletePipeline() {
		final DeleteRequest expectedDeleteRequest = new DeleteRequest(LDIO_HOST + "/admin/api/v1/pipeline/" + PIPELINE_NAME);

		ldioPipelineManager.deletePipeline(PIPELINE_NAME);

		verify(requestExecutor).execute(
				assertArg(actual -> assertThat(actual).usingRecursiveComparison().isEqualTo(expectedDeleteRequest)),
				eq(202), eq(204));
	}
}
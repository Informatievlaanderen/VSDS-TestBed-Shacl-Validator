package be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline;

import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.ldes.EventStreamProperties;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.pipeline.ValidationPipelineSupplier;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.IOException;

import static be.vlaanderen.informatievlaanderen.ldes.gitb.shacl.valueobjects.ValidationParameters.PIPELINE_NAME_TEMPLATE;
import static org.assertj.core.api.Assertions.assertThat;

class ValidationPipelineSupplierTest {
	private static final String LDES_SERVER_URL = "http://test-server/test-collection";
	private static final String SPARQL_HOST = "http://my-sparql-host.net";
	private static final String PIPELINE_UUID = "test-pipeline-uuid";
	private static final String PIPELINE_NAME = PIPELINE_NAME_TEMPLATE.formatted(PIPELINE_UUID);
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void test_createJson() throws IOException {
		final ValidationPipelineSupplier factory = new ValidationPipelineSupplier(new EventStreamProperties(LDES_SERVER_URL, "http://purl.org/dc/terms/isVersionOf"), SPARQL_HOST, PIPELINE_NAME);
		final JsonNode expectedJson = readJsonNode();

		final String result = factory.getValidationPipelineAsJson();
		final JsonNode actualJson = objectMapper.readTree(result);

		assertThat(actualJson)
				.isEqualTo(expectedJson);

	}

	private JsonNode readJsonNode() throws IOException {
		final var jsonFile = ResourceUtils.getFile("classpath:ldio-pipeline.json");
		return objectMapper.readTree(jsonFile);
	}
}
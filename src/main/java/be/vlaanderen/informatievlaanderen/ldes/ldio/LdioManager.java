package be.vlaanderen.informatievlaanderen.ldes.ldio;


import be.vlaanderen.informatievlaanderen.ldes.http.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.http.requests.DeleteRequest;
import be.vlaanderen.informatievlaanderen.ldes.http.requests.PostRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldes.services.EventStreamFetcher;
import be.vlaanderen.informatievlaanderen.ldes.ldes.valueobjects.EventStreamProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.ValidationPipelineSupplier;
import org.apache.http.entity.ContentType;
import org.springframework.stereotype.Service;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.ValidationPipelineSupplier.PIPELINE_NAME;

@Service
public class LdioManager {
	private final EventStreamFetcher eventStreamFetcher;
	private final RequestExecutor requestExecutor;
	private final LdioConfigProperties ldioConfigProperties;

	public LdioManager(EventStreamFetcher eventStreamFetcher, RequestExecutor requestExecutor, LdioConfigProperties ldioConfigProperties) {
		this.eventStreamFetcher = eventStreamFetcher;
		this.requestExecutor = requestExecutor;
		this.ldioConfigProperties = ldioConfigProperties;
	}

	public void initPipeline(String serverUrl) {
		final String ldioAdminPipelineUrl = ldioConfigProperties.getLdioAdminPipelineUrl();
		final EventStreamProperties eventStreamProperties = eventStreamFetcher.fetchProperties(serverUrl);
		final String json = new ValidationPipelineSupplier(eventStreamProperties, ldioConfigProperties.getSparqlHost()).createValidationPipelineAsJson();
		requestExecutor.execute(new PostRequest(ldioAdminPipelineUrl, json, ContentType.APPLICATION_JSON), 201);
	}

	public void deletePipeline() {
		requestExecutor.execute(
				new DeleteRequest(ldioConfigProperties.getLdioAdminPipelineUrl() + "/" + PIPELINE_NAME)
		);
	}

}

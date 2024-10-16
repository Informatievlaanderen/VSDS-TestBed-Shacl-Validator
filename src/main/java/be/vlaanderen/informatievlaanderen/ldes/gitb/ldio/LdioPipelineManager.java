package be.vlaanderen.informatievlaanderen.ldes.gitb.ldio;


import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.requests.DeleteRequest;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.requests.PostRequest;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.config.LdioConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.pipeline.ValidationPipelineSupplier;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LdioPipelineManager {
	private static final Logger log = LoggerFactory.getLogger(LdioPipelineManager.class);
	private final RequestExecutor requestExecutor;
	private final LdioConfigProperties ldioConfigProperties;

	public LdioPipelineManager(RequestExecutor requestExecutor, LdioConfigProperties ldioConfigProperties) {
		this.requestExecutor = requestExecutor;
		this.ldioConfigProperties = ldioConfigProperties;
	}

	public void initPipeline(String serverUrl, String pipelineName) {
		final String ldioAdminPipelineUrl = ldioConfigProperties.getLdioAdminPipelineUrl();
		final String json = new ValidationPipelineSupplier(serverUrl, ldioConfigProperties.getSparqlHost(), pipelineName).getValidationPipelineAsJson();
		requestExecutor.execute(new PostRequest(ldioAdminPipelineUrl, json, ContentType.APPLICATION_JSON), 201);
		log.atInfo().log("LDIO pipeline created: {}", pipelineName);
	}

	public void haltPipeline(String pipelineName) {
		log.atInfo().log("Halt of LDIO pipeline {} requested", pipelineName);
		final String ldioAdminPipelineUrl = "%s/%s/halt".formatted(ldioConfigProperties.getLdioAdminPipelineUrl(), pipelineName);
		requestExecutor.execute(new PostRequest(ldioAdminPipelineUrl, "", "*/*"), 200);
		log.atInfo().log("LDIO pipeline halted: {}", pipelineName);
	}

	public void deletePipeline(String pipelineName) {
		log.atInfo().log("Deletion of LDIO pipeline {} requested", pipelineName);
		requestExecutor.execute(
				new DeleteRequest(ldioConfigProperties.getLdioAdminPipelineUrl() + "/" + pipelineName), 202, 204
		);
		log.atInfo().log("LDIO pipeline deleted: {}", pipelineName);
	}

}

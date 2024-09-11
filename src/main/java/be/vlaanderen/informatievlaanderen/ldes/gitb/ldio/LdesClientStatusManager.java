package be.vlaanderen.informatievlaanderen.ldes.gitb.ldio;

import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.HttpResponse;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.requests.GetRequest;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.config.LdioConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.excpeptions.LdesClientStatusUnavailableException;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.valuebojects.ClientStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class LdesClientStatusManager {
	private final RequestExecutor requestExecutor;
	private final LdioConfigProperties ldioConfigProperties;
	private final ObjectMapper objectMapper;

	public LdesClientStatusManager(RequestExecutor requestExecutor, LdioConfigProperties ldioConfigProperties) {
		this.requestExecutor = requestExecutor;
		this.ldioConfigProperties = ldioConfigProperties;
		objectMapper = new ObjectMapper();
	}

	public ClientStatus getClientStatus(String pipelineName) {
		final String pollingUrl = ldioConfigProperties.getLdioLdesClientStatusUrlTemplate().formatted(pipelineName);
		final HttpResponse response = requestExecutor.execute(new GetRequest(pollingUrl), 200, 404);
		final String json = response.getBody().orElseThrow(LdesClientStatusUnavailableException::new);
		try {
			return objectMapper.readValue(json, ClientStatus.class);
		} catch (IOException e) {
			throw new IllegalStateException("Invalid client status received from %s".formatted(pollingUrl));
		}
	}
}

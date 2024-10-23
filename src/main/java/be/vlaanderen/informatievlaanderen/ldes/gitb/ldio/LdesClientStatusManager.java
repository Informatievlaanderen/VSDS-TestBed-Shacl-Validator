package be.vlaanderen.informatievlaanderen.ldes.gitb.ldio;

import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.config.LdioConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.excpeptions.LdesClientStatusUnavailableException;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.valuebojects.ClientStatus;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.HttpResponse;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.requests.GetRequest;
import org.springframework.stereotype.Service;

@Service
public class LdesClientStatusManager {
	private final RequestExecutor requestExecutor;
	private final LdioConfigProperties ldioConfigProperties;

	public LdesClientStatusManager(RequestExecutor requestExecutor, LdioConfigProperties ldioConfigProperties) {
		this.requestExecutor = requestExecutor;
		this.ldioConfigProperties = ldioConfigProperties;
	}

	public ClientStatus getClientStatus(String pipelineName) {
		final String pollingUrl = ldioConfigProperties.getLdioLdesClientStatusUrlTemplate().formatted(pipelineName);
		final HttpResponse response = requestExecutor.execute(new GetRequest(pollingUrl), 200, 404);
		return response.getBody().map(ClientStatus::valueOf).orElseThrow(LdesClientStatusUnavailableException::new);
	}
}

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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LdesClientStatusManager {
	private static final Logger log = LoggerFactory.getLogger(LdesClientStatusManager.class);
	private static final int POLLING_PERIOD_IN_SECONDS = 5;
	private static final int CLIENT_STATUS_FETCHING_RETRIES = 5;
	private final RequestExecutor requestExecutor;
	private final LdioConfigProperties ldioConfigProperties;
	private final ObjectMapper objectMapper;


	public LdesClientStatusManager(RequestExecutor requestExecutor, LdioConfigProperties ldioConfigProperties) {
		this.requestExecutor = requestExecutor;
		this.ldioConfigProperties = ldioConfigProperties;
		objectMapper = new ObjectMapper();
	}

	public void waitUntilReplicated(String pipelineName) {
		log.atInfo().log("Waiting for the LDES client to complete REPLICATING");
		CompletableFuture<Boolean> future = new CompletableFuture<>();
		final String ldesClientStatusPollingUrl = ldioConfigProperties.getLdioLdesClientStatusUrlTemplate().formatted(pipelineName);
		ReplicationTask replicationTask = new ReplicationTask(ldesClientStatusPollingUrl, requestExecutor, future);
		Timer timer = new Timer();
		timer.schedule(replicationTask, 0, 5000);
		future.exceptionally(e -> {
			log.atError().log("Something went wrong while waiting for LDES client to be fully replicated: {}", e.getMessage());
			return false;
		}).thenAccept(replicated -> timer.cancel()).join();
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

	static class ReplicationTask extends TimerTask {
		private final ObjectMapper objectMapper;
		private final String pollingUrl;
		private final RequestExecutor requestExecutor;
		private final CompletableFuture<Boolean> future;
		private final AtomicInteger retryCount;

		public ReplicationTask(String pollingUrl, RequestExecutor requestExecutor, CompletableFuture<Boolean> future) {
			this.pollingUrl = pollingUrl;
			this.requestExecutor = requestExecutor;
			this.future = future;
			retryCount = new AtomicInteger();
			objectMapper = new ObjectMapper();
		}

		@Override
		public void run() {
			try {
				final ClientStatus clientStatus = getClientStatus();
				retryCount.set(0);
				log.atDebug().log("Checking for LDES client status");
				if (ClientStatus.isSuccessfullyReplicated(clientStatus)) {
					log.atInfo().log("LDES client status is now {}", clientStatus.toString());
					future.complete(true);
				}
			} catch (LdesClientStatusUnavailableException e) {
				if (retryCount.incrementAndGet() == CLIENT_STATUS_FETCHING_RETRIES) {
					future.completeExceptionally(e);
				}
				log.atWarn().log("LDES client status for {} is not available yet at attempt {}, trying again in {} seconds ...", pollingUrl, retryCount.get(), POLLING_PERIOD_IN_SECONDS);
			} catch (Exception e) {
				future.completeExceptionally(e);
			}
		}

		public ClientStatus getClientStatus() {
			final HttpResponse response = requestExecutor.execute(new GetRequest(pollingUrl), 200, 404);
			final String json = response.getBody().orElseThrow(LdesClientStatusUnavailableException::new);
			try {
				return objectMapper.readValue(json, ClientStatus.class);
			} catch (IOException e) {
				throw new IllegalStateException("Invalid client status received from %s".formatted(pollingUrl));
			}
		}
	}
}

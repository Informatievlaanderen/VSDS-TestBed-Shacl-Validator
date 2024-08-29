package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.http.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.http.requests.GetRequest;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.excpeptions.LdesClientStatusUnavailableException;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valuebojects.ClientStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LdesClientStatusManager {
	private static final Logger log = LoggerFactory.getLogger(LdesClientStatusManager.class);
	private static final int POLLING_PERIOD_IN_SECONDS = 5;
	private static final int CLIENT_STATUS_FETCHING_RETRIES = 5;
	private final RequestExecutor requestExecutor;
	private final LdioConfigProperties ldioConfigProperties;
	private final TaskScheduler taskScheduler;

	public LdesClientStatusManager(RequestExecutor requestExecutor, LdioConfigProperties ldioConfigProperties, TaskScheduler taskScheduler) {
		this.requestExecutor = requestExecutor;
		this.ldioConfigProperties = ldioConfigProperties;
		this.taskScheduler = taskScheduler;
	}

	public void waitUntilReplicated(String pipelineName) {
		final CompletableFuture<Boolean> hasReplicated = new CompletableFuture<>();
		final AtomicInteger retryCount = new AtomicInteger();

		log.atInfo().log("Waiting for the LDES client to complete REPLICATING");
		final var scheduledFuture = taskScheduler.scheduleAtFixedRate(() -> {
			try {
				final ClientStatus clientStatus = getClientStatus(pipelineName);
				log.atDebug().log("Checking for LDES client status");
				if (ClientStatus.isSuccessfullyReplicated(clientStatus)) {
					log.atInfo().log("LDES client status is now {}", clientStatus.toString());
					hasReplicated.complete(true);
				}
			} catch (LdesClientStatusUnavailableException e) {
				if(retryCount.incrementAndGet() == CLIENT_STATUS_FETCHING_RETRIES) {
					hasReplicated.complete(false);
				}
				log.atWarn().log("LDES client status for pipeline {} is not available yet, trying again in {} seconds ...", pipelineName, POLLING_PERIOD_IN_SECONDS);
			} catch (Exception e) {
				hasReplicated.complete(false);
			}
		}, Duration.ofSeconds(POLLING_PERIOD_IN_SECONDS));

		try {
			if(Boolean.FALSE.equals(hasReplicated.get())) {
				throw new IllegalStateException("Unable to fetch the LDES client status");
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.atError().log("Thread interrupted", e);
		} catch (ExecutionException e) {
			log.atError().log("Something went wrong while waiting for LDES client to be fully replicated", e);
		} finally {
			scheduledFuture.cancel(true);
		}
	}

	public ClientStatus getClientStatus(String pipelineName) {
		final String clientStatusUrl = ldioConfigProperties.getLdioLdesClientStatusUrlTemplate().formatted(pipelineName);
		final HttpEntity response = requestExecutor.execute(new GetRequest(clientStatusUrl), 200, 404);

		if (response.getContentLength() == 0) {
			throw new LdesClientStatusUnavailableException();
		}

		final ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(response.getContent(), ClientStatus.class);
		} catch (IOException e) {
			throw new IllegalStateException("Invalid client status received from %s".formatted(clientStatusUrl));
		}
	}
}

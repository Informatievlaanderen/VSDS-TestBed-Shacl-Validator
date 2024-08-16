package be.vlaanderen.informatievlaanderen.ldes.ldio;

import be.vlaanderen.informatievlaanderen.ldes.http.Request;
import be.vlaanderen.informatievlaanderen.ldes.http.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldio.config.LdioConfigProperties;
import be.vlaanderen.informatievlaanderen.ldes.ldio.valuebojects.ClientStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class LdesClientStatusManager {
	private final RequestExecutor requestExecutor;
	private final LdioConfigProperties ldioConfigProperties;

	public LdesClientStatusManager(RequestExecutor requestExecutor, LdioConfigProperties ldioConfigProperties) {
		this.requestExecutor = requestExecutor;
		this.ldioConfigProperties = ldioConfigProperties;
	}

	public void waitUntilReplicated() {
		final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		scheduler.scheduleAtFixedRate(() -> {
			final ClientStatus clientStatus = getClientStatus();
			if (clientStatus.isSuccessfullyReplicated()) {
				countDownLatch.countDown();
			}
		}, 0, 5, TimeUnit.SECONDS);

		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			scheduler.shutdown();
		}
	}

	public ClientStatus getClientStatus() {
		final String clientStatusUrl = ldioConfigProperties.getLdioLdesClientStatusUrl();
		final HttpEntity response = requestExecutor.execute(new Request(clientStatusUrl, RequestMethod.GET));

		final ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(response.getContent(), ClientStatus.class);
		} catch (IOException e) {
			throw new IllegalStateException("Invalid client status received from %s".formatted(clientStatusUrl));
		}
	}
}

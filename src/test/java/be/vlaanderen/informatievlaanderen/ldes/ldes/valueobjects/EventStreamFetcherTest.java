package be.vlaanderen.informatievlaanderen.ldes.ldes.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.HttpResponse;
import be.vlaanderen.informatievlaanderen.ldes.gitb.requestexecutor.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.ldes.EventStreamFetcher;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ldio.ldes.EventStreamProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventStreamFetcherTest {
	@Mock
	private RequestExecutor requestExecutor;
	@InjectMocks
	private EventStreamFetcher eventStreamFetcher;

	@Test
	void test_FetchEventStream() throws IOException {
		final EventStreamProperties expected = new EventStreamProperties("http://test.com", "http://purl.org/dc/terms/isVersionOf");
		final String eventStreamProperties = Files.readString(ResourceUtils.getFile("classpath:event-stream.ttl").toPath());
		when(requestExecutor.execute(any())).thenReturn(new HttpResponse(200, eventStreamProperties));

		final EventStreamProperties actual = eventStreamFetcher.fetchProperties("http://test.com");

		assertThat(actual).isEqualTo(expected);
	}
}
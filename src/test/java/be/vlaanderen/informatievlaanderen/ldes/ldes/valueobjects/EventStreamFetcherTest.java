package be.vlaanderen.informatievlaanderen.ldes.ldes.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.http.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.ldes.services.EventStreamFetcher;
import org.apache.http.entity.BasicHttpEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileInputStream;
import java.io.IOException;

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
		final EventStreamProperties expected = new EventStreamProperties("http://test.com", "verkeersmetingen", "http://purl.org/dc/terms/isVersionOf");
		final BasicHttpEntity httpEntity = new BasicHttpEntity();
		httpEntity.setContent(new FileInputStream("src/test/resources/event-stream.ttl"));
		when(requestExecutor.execute(any())).thenReturn(httpEntity);

		final EventStreamProperties actual = eventStreamFetcher.fetchProperties("http://test.com");

		assertThat(actual).isEqualTo(expected);
	}
}
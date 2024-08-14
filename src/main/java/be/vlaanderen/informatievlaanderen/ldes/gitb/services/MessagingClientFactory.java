package be.vlaanderen.informatievlaanderen.ldes.gitb.services;

import com.gitb.ms.MessagingClient;

public interface MessagingClientFactory {
	MessagingClient createMessagingClient(String callbackAddress);
}

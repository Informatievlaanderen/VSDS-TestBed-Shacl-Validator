package be.vlaanderen.informatievlaanderen.ldes.gitb.services;

import com.gitb.ms.MessagingClient;

public interface MessagingClients {
	MessagingClient getOrCreateByCallbackAddress(String callbackAddress);
}

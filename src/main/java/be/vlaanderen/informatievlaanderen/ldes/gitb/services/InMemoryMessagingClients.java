package be.vlaanderen.informatievlaanderen.ldes.gitb.services;

import com.gitb.ms.MessagingClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryMessagingClients implements MessagingClients {
	private final ConcurrentHashMap<String, MessagingClient> clients = new ConcurrentHashMap<>();
	private final MessagingClientFactory messagingClientFactory;

	public InMemoryMessagingClients(MessagingClientFactory messagingClientFactory) {
		this.messagingClientFactory = messagingClientFactory;
	}

	@Override
	public MessagingClient getOrCreateByCallbackAddress(String callbackAddress) {
		return clients.computeIfAbsent(callbackAddress, messagingClientFactory::createMessagingClient);
	}
}

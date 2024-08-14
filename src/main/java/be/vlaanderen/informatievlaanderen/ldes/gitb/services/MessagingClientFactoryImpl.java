package be.vlaanderen.informatievlaanderen.ldes.gitb.services;

import be.vlaanderen.informatievlaanderen.ldes.gitb.services.httpconduitcustomizer.HttpConduitCustomizer;
import com.gitb.ms.MessagingClient;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.springframework.stereotype.Component;

@Component
public class MessagingClientFactoryImpl implements MessagingClientFactory {
	private final HttpConduitCustomizer httpConduitCustomizer;

	public MessagingClientFactoryImpl(HttpConduitCustomizer httpConduitCustomizer) {
		this.httpConduitCustomizer = httpConduitCustomizer;
	}

	@Override
	public MessagingClient createMessagingClient(String callbackAddress) {
		final JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
		proxyFactoryBean.setServiceClass(MessagingClient.class);
		proxyFactoryBean.setAddress(callbackAddress);

		final MessagingClient serviceProxy = proxyFactoryBean.create(MessagingClient.class);
		final Client client = ClientProxy.getClient(serviceProxy);
		final HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
		httpConduitCustomizer.customize(httpConduit);
		return serviceProxy;
	}
}

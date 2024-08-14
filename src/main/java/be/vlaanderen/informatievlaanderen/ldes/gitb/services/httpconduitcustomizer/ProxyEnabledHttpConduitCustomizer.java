package be.vlaanderen.informatievlaanderen.ldes.gitb.services.httpconduitcustomizer;

import be.vlaanderen.informatievlaanderen.ldes.gitb.config.valueobjects.ProxyProperties;
import org.apache.cxf.transport.http.HTTPConduit;

public class ProxyEnabledHttpConduitCustomizer implements HttpConduitCustomizer {
	private final ProxyProperties proxyProperties;

	public ProxyEnabledHttpConduitCustomizer(ProxyProperties proxyProperties) {
		this.proxyProperties = proxyProperties;
	}

	@Override
	public void customize(HTTPConduit httpConduit) {
		httpConduit.getClient().setProxyServer(proxyProperties.getServer());
		httpConduit.getClient().setProxyServerPort(proxyProperties.getPort());
		httpConduit.getClient().setProxyServerType(proxyProperties.getType());
		httpConduit.getClient().setNonProxyHosts(proxyProperties.getNonProxyHosts());
	}
}

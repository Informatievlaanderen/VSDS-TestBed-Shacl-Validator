package be.vlaanderen.informatievlaanderen.ldes.gitb.services.httpconduitcustomizer;

import be.vlaanderen.informatievlaanderen.ldes.gitb.config.valueobjects.ProxyAuthProperties;
import org.apache.cxf.configuration.security.ProxyAuthorizationPolicy;
import org.apache.cxf.transport.http.HTTPConduit;

public class ProxyAuthEnabledHttpConduitCustomizer implements HttpConduitCustomizer {
	private final HttpConduitCustomizer baseCustomizer;
	private final ProxyAuthProperties proxyAuthProperties;

	public ProxyAuthEnabledHttpConduitCustomizer(HttpConduitCustomizer baseCustomizer, ProxyAuthProperties proxyAuthProperties) {
		this.baseCustomizer = baseCustomizer;
		this.proxyAuthProperties = proxyAuthProperties;
	}

	@Override
	public void customize(HTTPConduit httpConduit) {
		baseCustomizer.customize(httpConduit);
		httpConduit.setProxyAuthorization(createPolicy());
	}

	private ProxyAuthorizationPolicy createPolicy() {
		final ProxyAuthorizationPolicy policy = new ProxyAuthorizationPolicy();
		policy.setUserName(proxyAuthProperties.getUsername());
		policy.setPassword(proxyAuthProperties.getPassword());
		return policy;
	}
}

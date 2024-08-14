package be.vlaanderen.informatievlaanderen.ldes.gitb.config.valueobjects;

import org.apache.cxf.transports.http.configuration.ProxyServerType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "proxy")
public class ProxyProperties {
	private boolean enabled = false;
	private String server;
	private int port;
	private ProxyServerType type = ProxyServerType.HTTP;
	private String nonProxyHosts;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public ProxyServerType getType() {
		return type;
	}

	public void setType(ProxyServerType type) {
		this.type = type;
	}

	public String getNonProxyHosts() {
		return nonProxyHosts;
	}

	public void setNonProxyHosts(String nonProxyHosts) {
		this.nonProxyHosts = nonProxyHosts;
	}
}

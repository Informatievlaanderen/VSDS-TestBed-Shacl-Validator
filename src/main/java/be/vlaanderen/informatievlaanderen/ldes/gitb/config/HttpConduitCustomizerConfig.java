package be.vlaanderen.informatievlaanderen.ldes.gitb.config;

import be.vlaanderen.informatievlaanderen.ldes.gitb.config.valueobjects.ProxyAuthProperties;
import be.vlaanderen.informatievlaanderen.ldes.gitb.config.valueobjects.ProxyProperties;
import be.vlaanderen.informatievlaanderen.ldes.gitb.services.httpconduitcustomizer.HttpConduitCustomizer;
import be.vlaanderen.informatievlaanderen.ldes.gitb.services.httpconduitcustomizer.ProxyAuthEnabledHttpConduitCustomizer;
import be.vlaanderen.informatievlaanderen.ldes.gitb.services.httpconduitcustomizer.ProxyEnabledHttpConduitCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class HttpConduitCustomizerConfig {

	private static final Logger log = LoggerFactory.getLogger(HttpConduitCustomizerConfig.class);
	private static final String LOGGING_TEMPLATE = "{} bean created";

	@Bean
	@Primary
	@ConditionalOnProperty(prefix = "proxy", name = {"enabled", "auth.enabled"}, havingValue = "true")
	public HttpConduitCustomizer proxyAuthCustomizer(HttpConduitCustomizer proxyCustomizer, ProxyAuthProperties authProperties) {
		final HttpConduitCustomizer customizer = new ProxyAuthEnabledHttpConduitCustomizer(proxyCustomizer, authProperties);
		log.atInfo().log(LOGGING_TEMPLATE, customizer.getClass().getSimpleName());
		return customizer;
	}

	@Bean
	@ConditionalOnProperty(prefix = "proxy", name = "enabled", havingValue = "true")
	public HttpConduitCustomizer proxyCustomizer(ProxyProperties proxyProperties) {
		final HttpConduitCustomizer customizer = new ProxyEnabledHttpConduitCustomizer(proxyProperties);
		log.atInfo().log(LOGGING_TEMPLATE, customizer.getClass().getSimpleName());
		return customizer;
	}

	@Bean
	@ConditionalOnMissingBean
	public HttpConduitCustomizer httpConduitCustomizer() {
		log.atInfo().log(LOGGING_TEMPLATE, "Default %s".formatted(HttpConduitCustomizer.class.getSimpleName()));
		return conduit -> {
		};
	}
}

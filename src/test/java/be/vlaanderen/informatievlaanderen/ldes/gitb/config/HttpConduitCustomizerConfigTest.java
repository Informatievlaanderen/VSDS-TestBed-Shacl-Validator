package be.vlaanderen.informatievlaanderen.ldes.gitb.config;

import be.vlaanderen.informatievlaanderen.ldes.gitb.config.valueobjects.ProxyAuthProperties;
import be.vlaanderen.informatievlaanderen.ldes.gitb.config.valueobjects.ProxyProperties;
import be.vlaanderen.informatievlaanderen.ldes.gitb.services.httpconduitcustomizer.HttpConduitCustomizer;
import be.vlaanderen.informatievlaanderen.ldes.gitb.services.httpconduitcustomizer.ProxyAuthEnabledHttpConduitCustomizer;
import be.vlaanderen.informatievlaanderen.ldes.gitb.services.httpconduitcustomizer.ProxyEnabledHttpConduitCustomizer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class HttpConduitCustomizerConfigTest {
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withUserConfiguration(HttpConduitCustomizerConfig.class)
			.withBean(ProxyProperties.class)
			.withBean(ProxyAuthProperties.class);

	@Test
	void given_ProxyEnabled_when_GetHttpConduitCustomizerBean_then_BeanIsInstanceOfProxyEnabledHttpConduitCustomizer() {
		contextRunner
				.withPropertyValues("proxy.enabled=true")
				.run(context -> assertThat(context)
						.hasSingleBean(ProxyEnabledHttpConduitCustomizer.class)
						.doesNotHaveBean(ProxyAuthEnabledHttpConduitCustomizer.class));
	}

	@Test
	void given_ProxyEnabledAndProxyAuthEnabled_when_GetHttpConduitCustomizerBean_then_BeanIsInstanceOfProxyAuthEnabledHttpConduitCustomizer() {
		contextRunner
				.withPropertyValues("proxy.enabled=true", "proxy.auth.enabled=true")
				.run(context -> assertThat(context)
						.getBean(HttpConduitCustomizer.class)
						.isInstanceOf(ProxyAuthEnabledHttpConduitCustomizer.class)
						.isNotInstanceOf(ProxyEnabledHttpConduitCustomizer.class));
	}

	@Test
	void given_ProxyAuthEnabledAndProxyDisabled_when_GetHttpConduitCustomizerBean_then_StartupFails() {
		contextRunner
				.withPropertyValues("proxy.enabled=false", "proxy.auth.enabled=true")
				.run(context -> assertThat(context)
						.doesNotHaveBean(ProxyEnabledHttpConduitCustomizer.class)
						.doesNotHaveBean(ProxyAuthEnabledHttpConduitCustomizer.class));
	}

	@Test
	void given_ProxyDisabled_when_GetHttpCustomizerBean_then_BeanIsNotInstanceOfAnyDefinedHttpConduitCustomizerClass() {
		contextRunner
				.run(context -> assertThat(context)
						.doesNotHaveBean(ProxyEnabledHttpConduitCustomizer.class)
						.doesNotHaveBean(ProxyAuthEnabledHttpConduitCustomizer.class));
	}
}
package be.vlaanderen.informatievlaanderen.ldes.ldio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static be.vlaanderen.informatievlaanderen.ldes.ldio.pipeline.ValidationPipelineSupplier.PIPELINE_NAME;

@Configuration
@ConfigurationProperties(prefix = "ldio")
public class LdioConfigProperties {
	public static final String REPOSITORY_ID = "validation";

	private String host;
	private String sparqlHost;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getSparqlHost() {
		return sparqlHost;
	}

	public void setSparqlHost(String sparqlHost) {
		this.sparqlHost = sparqlHost;
	}

	public String getLdioAdminPipelineUrl() {
		return "%s/admin/api/v1/pipeline".formatted(host);
	}

	public String getLdioLdesClientStatusUrl() {
		return "%s/ldes-client/%s".formatted(getLdioAdminPipelineUrl(), PIPELINE_NAME);
	}

	public String getRepositoryValidationUrl() {
		return "%s/rest/repositories/%s/validate/text".formatted(sparqlHost, REPOSITORY_ID);
	}
}

package be.vlaanderen.informatievlaanderen.ldes.config;

import be.vlaanderen.informatievlaanderen.ldes.http.RequestExecutor;
import be.vlaanderen.informatievlaanderen.ldes.rdfrepo.RepositoryValidator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties()
@ComponentScan("be.vlaanderen.informatievlaanderen.ldes")
public class ShaclValidationConfig {

    @Bean
    public RequestExecutor requestExecutor() {
        return new RequestExecutor();
    }
    @Bean
    public RepositoryValidator repositoryValidator() {
        return new RepositoryValidator();
    }

}

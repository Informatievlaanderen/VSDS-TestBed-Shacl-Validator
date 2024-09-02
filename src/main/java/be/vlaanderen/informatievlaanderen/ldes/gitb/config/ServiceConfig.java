package be.vlaanderen.informatievlaanderen.ldes.gitb.config;

import be.vlaanderen.informatievlaanderen.ldes.gitb.ValidationServiceImpl;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;

/**
 * Configuration class responsible for creating the Spring beans required by the service.
 */
@Configuration
public class ServiceConfig {

    /**
     * The CXF endpoint that will serve validation service calls.
     *
     * @return The endpoint.
     */
    @Bean
    public EndpointImpl validationService(Bus cxfBus, ValidationServiceImpl validationServiceImplementation) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, validationServiceImplementation);
        endpoint.setServiceName(new QName("http://www.gitb.com/vs/v1/", "ValidationService"));
        endpoint.setEndpointName(new QName("http://www.gitb.com/vs/v1/", "ValidationServicePort"));
        endpoint.publish("/validation");
        return endpoint;
    }

}

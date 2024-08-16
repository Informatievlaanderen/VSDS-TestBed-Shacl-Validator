package be.vlaanderen.informatievlaanderen.ldes.gitb;

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
     * The CXF endpoint that will serve messaging service calls.
     *
     * @return The endpoint.
     */
    @Bean
    public EndpointImpl messagingService(Bus cxfBus, MessagingServiceImpl messagingServiceImplementation) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, messagingServiceImplementation);
        endpoint.setServiceName(new QName("http://www.gitb.com/ms/v1/", "MessagingServiceService"));
        endpoint.setEndpointName(new QName("http://www.gitb.com/ms/v1/", "MessagingServicePort"));
        endpoint.publish("/messaging");
        return endpoint;
    }

    /**
     * The CXF endpoint that will serve processing service calls.
     *
     * @return The endpoint.
     */
    @Bean
    public EndpointImpl processingService(Bus cxfBus, ProcessingServiceImpl processingServiceImplementation) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, processingServiceImplementation);
        endpoint.setServiceName(new QName("http://www.gitb.com/ps/v1/", "ProcessingServiceService"));
        endpoint.setEndpointName(new QName("http://www.gitb.com/ps/v1/", "ProcessingServicePort"));
        endpoint.publish("/process");
        return endpoint;
    }

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

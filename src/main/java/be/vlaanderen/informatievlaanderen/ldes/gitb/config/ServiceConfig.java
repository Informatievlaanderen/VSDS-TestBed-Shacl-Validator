package be.vlaanderen.informatievlaanderen.ldes.gitb.config;

import be.vlaanderen.informatievlaanderen.ldes.gitb.ReplicationProcessingService;
import be.vlaanderen.informatievlaanderen.ldes.gitb.ShaclValidationService;
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

    @Bean
    public EndpointImpl validationService(Bus cxfBus, ShaclValidationService shaclValidationServiceImplementation) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, shaclValidationServiceImplementation);
        endpoint.setServiceName(new QName("http://www.gitb.com/vs/v1/", "ValidationService"));
        endpoint.setEndpointName(new QName("http://www.gitb.com/vs/v1/", "ValidationServicePort"));
        endpoint.publish("/validation");
        return endpoint;
    }

    @Bean
    public EndpointImpl processingService(Bus cxfBus, ReplicationProcessingService replicationProcessingService) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, replicationProcessingService);
        endpoint.setServiceName(new QName("http://www.gitb.com/ps/v1/", "ProcessingServiceService"));
        endpoint.setEndpointName(new QName("http://www.gitb.com/ps/v1/", "ProcessingServicePort"));
        endpoint.publish("/process");
        return endpoint;
    }

}

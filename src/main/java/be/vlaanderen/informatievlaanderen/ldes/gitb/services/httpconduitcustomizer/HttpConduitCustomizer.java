package be.vlaanderen.informatievlaanderen.ldes.gitb.services.httpconduitcustomizer;

import org.apache.cxf.transport.http.HTTPConduit;

public interface HttpConduitCustomizer {
	void customize(HTTPConduit conduit);
}

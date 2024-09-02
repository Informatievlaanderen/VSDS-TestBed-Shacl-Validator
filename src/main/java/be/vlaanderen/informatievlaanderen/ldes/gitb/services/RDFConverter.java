package be.vlaanderen.informatievlaanderen.ldes.gitb.services;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

public class RDFConverter {
	private RDFConverter() {
	}

    public static String writeModel(Model model, RDFFormat format) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Rio.write(model, out, format);
        return out.toString();
    }

    public static Model readModel(String content, RDFFormat format)  {
	    try {
		    return Rio.parse(new ByteArrayInputStream(content.getBytes()), format);
	    } catch (IOException e) {
			throw new UncheckedIOException(e);
	    }
    }
}

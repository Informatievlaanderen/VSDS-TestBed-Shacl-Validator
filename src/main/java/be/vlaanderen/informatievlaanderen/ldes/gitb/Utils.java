package be.vlaanderen.informatievlaanderen.ldes.gitb;

import com.gitb.core.AnyContent;
import com.gitb.core.ValueEmbeddingEnumeration;
import jakarta.xml.ws.WebServiceContext;
import org.apache.cxf.headers.Header;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;


public class Utils {

	/**
	 * SOAP header name for the ReplyTo address.
	 */
	public static final QName REPLY_TO_QNAME = new QName("http://www.w3.org/2005/08/addressing", "ReplyTo");
	/**
	 * SOAP header name for the test session ID.
	 */
	public static final QName TEST_SESSION_ID_QNAME = new QName("http://www.gitb.com", "TestSessionIdentifier", "gitb");


	private Utils() {
	}

	/**
	 * Create a AnyContent object value based on the provided parameters.
	 *
	 * @param name            The name of the value.
	 * @param value           The value itself.
	 * @param embeddingMethod The way in which this value is to be considered.
	 * @return The value.
	 */
	public static AnyContent createAnyContentSimple(String name, String value, ValueEmbeddingEnumeration embeddingMethod) {
		AnyContent input = new AnyContent();
		input.setName(name);
		input.setValue(value);
		input.setType("string");
		input.setEmbeddingMethod(embeddingMethod);
		return input;
	}

	/**
	 * Parse the received SOAP headers to retrieve the "reply-to" address.
	 *
	 * @param context The call's context.
	 * @return The header's value.
	 */
	public static Optional<String> getReplyToAddressFromHeaders(WebServiceContext context) {
		return getHeaderAsString(context, REPLY_TO_QNAME).map(h -> {
			if (h.endsWith("?wsdl")) {
				return h;
			} else {
				return h + "?wsdl";
			}
		});
	}

	/**
	 * Parse the received SOAP headers to retrieve the test session identifier.
	 *
	 * @param context The call's context.
	 * @return The header's value.
	 */
	public static Optional<String> getTestSessionIdFromHeaders(WebServiceContext context) {
		return getHeaderAsString(context, TEST_SESSION_ID_QNAME);
	}

	/**
	 * Extract a value from the SOAP headers.
	 *
	 * @param name           The name of the header to locate.
	 * @param valueExtractor The function used to extract the data.
	 * @param <T>            The type of data extracted.
	 * @return The extracted data.
	 */
	public static  <T> T getHeaderValue(WebServiceContext context, QName name, Function<Header, T> valueExtractor) {
		return ((List<Header>) context.getMessageContext().get(Header.HEADER_LIST))
				.stream()
				.filter(header -> name.equals(header.getName())).findFirst()
				.map(valueExtractor).orElse(null);
	}

	/**
	 * Get the specified header element as a string.
	 *
	 * @param name The name of the header element to lookup.
	 * @return The text value of the element.
	 */
	public static Optional<String> getHeaderAsString(WebServiceContext context, QName name) {
		return Optional.ofNullable(getHeaderValue(context, name, header -> ((Element) header.getObject()).getTextContent().trim()));
	}


}

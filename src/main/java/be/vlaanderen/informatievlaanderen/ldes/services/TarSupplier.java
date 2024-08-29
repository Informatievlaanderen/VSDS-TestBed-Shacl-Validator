package be.vlaanderen.informatievlaanderen.ldes.services;

import com.gitb.core.AnyContent;
import com.gitb.tr.TAR;
import com.gitb.tr.TestResultType;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.GregorianCalendar;
import java.util.function.Supplier;

public class TarSupplier implements Supplier<TAR> {
	private final TestResultType testResultType;

	private TarSupplier(TestResultType testResultType) {
		this.testResultType = testResultType;
	}

	@Override
	public TAR get() {
		final TAR tar = new TAR();
		final AnyContent context = new AnyContent();
		context.setType("map");
		tar.setContext(context);
		tar.setResult(testResultType);
		try {
			tar.setDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
		} catch (DatatypeConfigurationException e) {
			throw new IllegalStateException(e);
		}
		return tar;
	}

	public static TAR success() {
		return new TarSupplier(TestResultType.SUCCESS).get();
	}

	public static TAR warning() {
		return new TarSupplier(TestResultType.WARNING).get();
	}

	public static TAR failure() {
		return new TarSupplier(TestResultType.FAILURE).get();
	}
}

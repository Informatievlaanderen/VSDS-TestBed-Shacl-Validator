package be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.gitb.shacl.valueobjects.ValidationReport;
import be.vlaanderen.informatievlaanderen.ldes.gitb.shacl.valueobjects.severitylevels.SeverityLevels;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationReportTest {

	@ParameterizedTest(name = "ShaclValidationReportFile={0}")
	@ArgumentsSource(ShaclValidationReportProvider.class)
	void test_ValidationReport(String resourceName, Consumer<ValidationReport> throwingConsumer) throws IOException {
		final Model shaclReport = Rio.parse(this.getClass().getClassLoader().getResourceAsStream(resourceName), RDFFormat.TURTLE);
		final ValidationReport validationReport = new ValidationReport(shaclReport);

		assertThat(validationReport).satisfies(throwingConsumer);
	}


	static class ShaclValidationReportProvider implements ArgumentsProvider {
		@Override
		public Stream<Arguments> provideArguments(ExtensionContext extensionContext) {
			return Stream.of(
					Arguments.of(
							"validation-report/invalid.ttl",
							(Consumer<ValidationReport>) actualValidationReport -> {
								assertThat(actualValidationReport.getHighestSeverityLevel()).isSameAs(SeverityLevels.ERROR);
								assertThat(actualValidationReport.errorCount()).isEqualTo(2);
								assertThat(actualValidationReport.warningCount()).isZero();
								assertThat(actualValidationReport.infoCount()).isZero();
							}
					),
					Arguments.of(
							"validation-report/valid.ttl",
							(Consumer<ValidationReport>) acutalValidationReport -> {
								assertThat(acutalValidationReport.getHighestSeverityLevel()).isSameAs(SeverityLevels.INFO);
								assertThat(acutalValidationReport.errorCount()).isZero();
								assertThat(acutalValidationReport.warningCount()).isZero();
								assertThat(acutalValidationReport.infoCount()).isZero();
							}
					)
			);
		}
	}
}
package be.vlaanderen.informatievlaanderen.ldes.gitb;

import be.vlaanderen.informatievlaanderen.ldes.gitb.services.replication.ProcessExecutors;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ParameterDefinition;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessParameters;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessResult;
import com.gitb.core.ConfigurationParameters;
import com.gitb.core.Metadata;
import com.gitb.ps.Void;
import com.gitb.ps.*;
import org.springframework.stereotype.Component;

@Component
public class ReplicationProcessingService implements ProcessingService {
	private static final String SERVICE_NAME = "ReplicationProcessingService";
	private final ProcessExecutors processExecutors;

	public ReplicationProcessingService(ProcessExecutors processExecutors) {
		this.processExecutors = processExecutors;
	}

	@Override
	public GetModuleDefinitionResponse getModuleDefinition(Void parameters) {
		final ProcessingModule processingModule = new ProcessingModule();
		processingModule.setId(SERVICE_NAME);

		final Metadata metadata = new Metadata();
		metadata.setName(SERVICE_NAME);
		processingModule.setMetadata(metadata);

		processingModule.setConfigs(new ConfigurationParameters());

		processExecutors.getProcessExecutors().stream()
				.map(processExecutor -> {
					final var processingOperation = new ProcessingOperation();
					processingOperation.setName(processExecutor.getName());
					processingOperation.getInputs().getParam().addAll(processExecutor
							.getParameterDefinitions()
							.stream()
							.map(ParameterDefinition::convertToTypedParameter)
							.toList());
					return processingOperation;
				})
				.forEach(processingModule.getOperation()::add);

		final GetModuleDefinitionResponse getModuleDefinitionResponse = new GetModuleDefinitionResponse();
		getModuleDefinitionResponse.setModule(processingModule);
		return getModuleDefinitionResponse;
	}

	@Override
	public ProcessResponse process(ProcessRequest parameters) {
		return processExecutors.getProcessExecutor(parameters.getOperation())
				.map(processExecutor -> processExecutor.execute(new ProcessParameters(parameters.getSessionId(), parameters.getInput())))
				.orElse(ProcessResult.invalidOperation(parameters.getOperation()))
				.convertToResponse();
	}

	@Override
	public BeginTransactionResponse beginTransaction(BeginTransactionRequest parameters) {
		System.out.println("BEGIN TRANSACTION");
		return null;
	}

	@Override
	public Void endTransaction(BasicRequest parameters) {
		System.out.println("END TRANSACTION");
		return null;
	}


}

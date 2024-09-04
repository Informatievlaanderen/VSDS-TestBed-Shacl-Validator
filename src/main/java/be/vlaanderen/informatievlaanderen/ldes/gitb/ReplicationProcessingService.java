package be.vlaanderen.informatievlaanderen.ldes.gitb;

import be.vlaanderen.informatievlaanderen.ldes.gitb.services.replication.ProcessExecutor;
import be.vlaanderen.informatievlaanderen.ldes.gitb.services.replication.ProcessExecutors;
import be.vlaanderen.informatievlaanderen.ldes.gitb.services.suppliers.TarSupplier;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.Parameters;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessParameters;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessResult;
import com.gitb.ps.*;
import com.gitb.ps.Void;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Component;

@Component
public class ReplicationProcessingService implements ProcessingService {
	private final ProcessExecutors processExecutors;

	public ReplicationProcessingService(ProcessExecutors processExecutors) {
		this.processExecutors = processExecutors;
	}

	@Override
	public GetModuleDefinitionResponse getModuleDefinition(Void parameters) {
		return null;
	}

	@Override
	public ProcessResponse process(ProcessRequest parameters) {
		final ProcessExecutor processExecutor;
		try {
			processExecutor = processExecutors.getProcessExecutor(parameters.getOperation());
		} catch (NoSuchBeanDefinitionException e) {
			return ProcessResult.invalidOperation(e.getBeanName()).convertToResponse();
		}
		return processExecutor
				.execute(new ProcessParameters(parameters.getSessionId(), new Parameters(parameters.getInput())))
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

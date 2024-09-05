package be.vlaanderen.informatievlaanderen.ldes.gitb.services.replication;

import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ParameterDefinition;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessParameters;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessResult;

import java.util.List;

public interface ProcessExecutor {
	String getName();
	List<ParameterDefinition> getParameterDefinitions();
	ProcessResult execute(ProcessParameters processParameters);
}

package be.vlaanderen.informatievlaanderen.ldes.gitb.services.replication;

import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessParameters;
import be.vlaanderen.informatievlaanderen.ldes.gitb.valueobjects.ProcessResult;
import com.gitb.tr.TAR;

public interface ProcessExecutor {
	ProcessResult execute(ProcessParameters processParameters);
}

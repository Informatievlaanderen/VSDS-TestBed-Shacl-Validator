package be.vlaanderen.informatievlaanderen.ldes.gitb.services.replication;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Component
public class ProcessExecutors {
	private final Map<String, ProcessExecutor> processExecutorBeans;

	public ProcessExecutors(ApplicationContext applicationContext) {
		processExecutorBeans = applicationContext.getBeansOfType(ProcessExecutor.class);
	}

	public Optional<ProcessExecutor> getProcessExecutor(String name) {
		return Optional.ofNullable(processExecutorBeans.get(name));
	}

	public Collection<ProcessExecutor> getProcessExecutors() {
		return processExecutorBeans.values();
	}
}

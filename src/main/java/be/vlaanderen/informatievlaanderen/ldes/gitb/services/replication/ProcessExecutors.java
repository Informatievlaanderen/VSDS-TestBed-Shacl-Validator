package be.vlaanderen.informatievlaanderen.ldes.gitb.services.replication;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class ProcessExecutors {
	private final Map<String, ProcessExecutor> processExecutorBeans;


	public ProcessExecutors(ApplicationContext applicationContext) {
		processExecutorBeans = applicationContext.getBeansOfType(ProcessExecutor.class);
	}

	public ProcessExecutor getProcessExecutor(String name) {
		final ProcessExecutor processExecutor = processExecutorBeans.get(name);
		if(processExecutor == null) {
			throw new NoSuchBeanDefinitionException(name);
		}
		return processExecutor;
	}

	public Set<String> getProcessExecutorNames() {
		return processExecutorBeans.keySet();
	}
}

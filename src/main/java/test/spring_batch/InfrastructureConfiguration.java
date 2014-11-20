package test.spring_batch;

import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;

public interface InfrastructureConfiguration {

	@Bean
	public abstract TaskExecutor taskExecutor();

}

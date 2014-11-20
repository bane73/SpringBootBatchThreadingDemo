package test.spring_batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableBatchProcessing
public class StandaloneInfrastructureConfiguration implements InfrastructureConfiguration {

	private static final int MAX_THREADS = 8;
	private static final int MIN_THREADS = 4;

	@Override
	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize(MAX_THREADS);
		taskExecutor.setCorePoolSize(MIN_THREADS);
		taskExecutor.afterPropertiesSet();
		return taskExecutor;
	}

}

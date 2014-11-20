package test.spring_batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import test.spring_batch.FlatFileToDbJobConfiguration.ProtocolListener;

//@Configuration
public class MultiThreadedStepConfig {

	@Autowired
	private JobBuilderFactory jobBuilders;

	@Autowired
	private StepBuilderFactory stepBuilders;

	@Autowired
	private InfrastructureConfiguration infrastructureConfiguration;

	@Bean
	public Job multiThreadedStepJob() {
		return jobBuilders.get("multiThreadedStepJob")
				.listener(protocolListener())
				.start(step())
				.build();
	}

	@Bean
	public Step step() {
		return stepBuilders.get("step")
				.<Model, Model> chunk(1)
				.reader(reader())
				.writer(writer())
				.taskExecutor(infrastructureConfiguration.taskExecutor())
				.throttleLimit(1)
				.build();
	}

	@Bean
	public Reader reader() {
		return new Reader();
	}

	@Bean
	public Writer writer() {
		return new Writer();
	}

	@Bean
	public ProtocolListener protocolListener() {
		return new ProtocolListener();
	}

}

package test.spring_batch;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

//@Configuration
public class FlatFileToDbJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilders;

	@Autowired
	private StepBuilderFactory stepBuilders;

	@Autowired
	private InfrastructureConfiguration infrastructureConfiguration;

	@Bean
	public Job flatfileToDbJob() {
		return jobBuilders.get("flatfileToDbJob")
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
				.listener(logProcessListener())
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

	@Bean
	public LogProcessListener logProcessListener() {
		return new LogProcessListener();
	}

	public static class ProtocolListener implements JobExecutionListener {

		@Override
		public void beforeJob(JobExecution jobExecution) {
			System.out.println("beforeJob()");
		}

		@Override
		public void afterJob(JobExecution jobExecution) {
			System.out.println("afterJob()");
		}

	}

	public static class LogProcessListener implements StepExecutionListener {

		@Override
		public void beforeStep(StepExecution stepExecution) {
			System.out.println("beforeStep()");
		}

		@Override
		public ExitStatus afterStep(StepExecution stepExecution) {
			System.out.println("afterStep()");
			return null;
		}

	}

}

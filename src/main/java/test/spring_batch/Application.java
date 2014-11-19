package test.spring_batch;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableBatchProcessing
public class Application {

	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;

	@Bean
	public Job myJob() throws Exception {
		JobBuilder jb = jobs.get("myJob");
		Step step = myStep();
		SimpleJobBuilder sjb = jb.start(step);
		Job job = sjb.build();

		return job;
	}

	@Bean
	public Step myStep() {

		StepBuilder sb = this.steps.get("myStep");

		/*
		 * This completion policy is called on every Reader.read() to see if we're
		 * done reading a file; if so, the Writer.write() is called.
		 * 
		 * ie: see isComplete()
		 */
		CompletionPolicy cp = new CompletionPolicy() {

			@Override
			public void update(RepeatContext context) {
				System.out.println("update");
			}

			@Override
			public RepeatContext start(RepeatContext parent) {
				System.out.println("start");
				return parent;
			}

			@Override
			public boolean isComplete(RepeatContext context, RepeatStatus result) {
				System.out.println("isComplete");
				return reader.isDoneWithFile();
			}

			@Override
			public boolean isComplete(RepeatContext context) {
				System.out.println("isComplete");
				return false;
			}
		};
		SimpleStepBuilder<Model, Model> ssb = sb.<Model, Model> chunk(cp);

		ssb.reader(reader);
		ssb.writer(writer);

		// no matter that I set a task-executor here, still seeing this in the logs:
		// "No TaskExecutor has been set, defaulting to synchronous executor"
		ssb.taskExecutor(taskExecutor());

		TaskletStep step = ssb.build();

		return step;
	}

	@Autowired
	public TaskExecutor taskExecutor;

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

		// no matter how many threads I supply here, execution-time is roughly static
		taskExecutor.setMaxPoolSize(1);

		taskExecutor.afterPropertiesSet();
		return taskExecutor;
	}

	@Autowired
	public Reader reader;

	@StepScope
	@Bean
	public Reader reader() {
		return new Reader();
	}

	@Autowired
	public Writer writer;

	@Bean
	public Writer writer() {
		return new Writer();
	}

	public static void main(String[] args) throws Exception {
		long start = new Date().getTime();

		ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);

		long stop = new Date().getTime();
		long tt = stop - start;
		System.out.println("Total time: " + tt + "ms");

		System.exit(SpringApplication.exit(ctx));
	}
}

package test.spring_batch;

import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourcePatternResolver;

@Configuration
public class PartitioningConfig {

	@Autowired
	private ResourcePatternResolver resourcePatternResolver;

	@Autowired
	private JobBuilderFactory jobBuilders;

	@Autowired
	private StepBuilderFactory stepBuilders;

	@Autowired
	private InfrastructureConfiguration infrastructureConfiguration;

	@Bean
	public Job flatfileToDbPartitioningJob() {
		return jobBuilders.get("flatfileToDbPartitioningJob")
				.start(partitionStep())
				.build();
	}

	@Bean
	public Step partitionStep() {
		return stepBuilders.get("partitionStep")
				.partitioner(flatfileToDbStep())
				.partitioner("flatfileToDbStep", partitioner())
				.taskExecutor(infrastructureConfiguration.taskExecutor())
				.build();
	}

	@Bean
	public Step flatfileToDbStep() {
		return stepBuilders.get("flatfileToDbStep")
				.<Model, Model> chunk(1)
				.reader(reader)
				.writer(writer())
				.build();
	}

	@Bean
	public Partitioner partitioner() {
		MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
		Resource[] resources;
		try {
			resources = resourcePatternResolver.getResources("file:src/main/resources/*.csv");
		} catch (IOException e) {
			throw new RuntimeException("I/O problems when resolving the input file pattern", e);
		}
		partitioner.setResources(resources);
		return partitioner;
	}

	@Autowired
	private FlatFileItemReader<Model> reader;

	@StepScope
	@Bean
	public FlatFileItemReader<Model> reader(@Value("#{stepExecutionContext['fileName']}") String filename) {
		FlatFileItemReader<Model> itemReader = new FlatFileItemReader<Model>();
		itemReader.setLineMapper(lineMapper());
		// itemReader.setResource(new ClassPathResource("model-import.csv"));
		try {
			itemReader.setResource(new UrlResource(filename));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		return itemReader;
	}

	@Bean
	public LineMapper<Model> lineMapper() {
		DefaultLineMapper<Model> lineMapper = new DefaultLineMapper<Model>();

		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setNames(new String[] {
				"name",
				"email"
		});
		lineTokenizer.setIncludedFields(new int[] {
				0,
				2
		});

		BeanWrapperFieldSetMapper<Model> fieldSetMapper = new BeanWrapperFieldSetMapper<Model>();
		fieldSetMapper.setTargetType(Model.class);

		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);

		return lineMapper;
	}

	@Bean
	public Writer writer() {
		return new Writer();
	}

}

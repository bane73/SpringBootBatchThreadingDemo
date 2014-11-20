package test.spring_batch;

import java.util.Date;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan
public class Application {

	public static void main(String[] args) throws Exception {
		long start = new Date().getTime();

		ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);

		long stop = new Date().getTime();
		long tt = stop - start;
		System.out.println("Total time: " + tt + "ms");

		System.exit(SpringApplication.exit(ctx));
	}

}

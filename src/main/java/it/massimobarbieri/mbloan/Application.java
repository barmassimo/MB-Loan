package it.massimobarbieri.mbloan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@EnableAutoConfiguration
@Configuration
@ComponentScan
public class Application {

	public static void main(String[] args) throws Throwable {
		new SpringApplication(Application.class).run(args);
	}
}
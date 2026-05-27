package org.example.api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

//	@Bean
//	CommandLineRunner flywayCheck(org.flywaydb.core.Flyway flyway) {
//		return args -> {
//			System.out.println("FLYWAY VERSION: " + flyway.info().current());
//		};
//	}

}

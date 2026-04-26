package com.davivienda.factoraje;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableCaching
@SpringBootApplication
@EnableScheduling
public class FactorajeApplication {

	public static void main(String[] args) {
		SpringApplication.run(FactorajeApplication.class, args);
	}
}
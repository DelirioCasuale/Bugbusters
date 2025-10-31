package com.generation.Bugbusters;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // abilita il supporto per le attività pianificate
public class BugbustersApplication {

	public static void main(String[] args) {
		SpringApplication.run(BugbustersApplication.class, args);
	}

}

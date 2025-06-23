package com.projeto.tiajuda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TiajudaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiajudaApplication.class, args);
	}

}

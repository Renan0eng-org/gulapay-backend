package br.unipar.foodservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class FoodServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodServiceApplication.class, args);
    }
}

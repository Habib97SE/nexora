package io.nexora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "io.nexora.catalog.infrastructure")
public class NexoraApplication {

    public static void main(String[] args) {
        SpringApplication.run(NexoraApplication.class, args);
    }

}

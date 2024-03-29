package com.credential.cubrism.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class CubrismApplication {

    public static void main(String[] args) {
        SpringApplication.run(CubrismApplication.class, args);
    }

}

package com.example.AutumnMall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled=true)
@EnableJpaAuditing
public class AutumnMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutumnMallApplication.class, args);
    }

}

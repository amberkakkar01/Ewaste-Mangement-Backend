package com.groupfive.ewastemanagement.config;

import com.groupfive.ewastemanagement.config.audit.AuditorAwareImpl;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableAutoConfiguration
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@ComponentScan({"com.groupfive.ewastemanagement"})
public class PersistenceConfig {
    @Bean("auditorProvider")
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }
}
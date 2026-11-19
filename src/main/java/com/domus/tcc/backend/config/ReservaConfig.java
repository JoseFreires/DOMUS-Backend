package com.domus.tcc.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class ReservaConfig {

    @Bean
    public Clock reservaClock() {
        return Clock.system(ZoneId.of("America/Sao_Paulo"));
    }
}

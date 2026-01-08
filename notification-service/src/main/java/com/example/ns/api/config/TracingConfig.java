package com.example.ns.api.config;

import io.micrometer.observation.*;
import org.springframework.context.annotation.*;

@Configuration
public class TracingConfig {

    @Bean
    ObservationRegistry observationRegistry() {
        return ObservationRegistry.create();
    }
}
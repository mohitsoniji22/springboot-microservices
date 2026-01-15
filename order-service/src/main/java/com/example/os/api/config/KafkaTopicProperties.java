package com.example.os.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.kafka.topics")
public class KafkaTopicProperties {

    private String paymentSuccess;
    private String paymentFailed;
}


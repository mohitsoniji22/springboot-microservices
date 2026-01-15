package com.example.os.api.config;

import org.apache.kafka.clients.admin.*;
import org.springframework.context.annotation.*;
import org.springframework.kafka.config.*;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic paymentSuccessTopic() {
        return TopicBuilder.name("payment.success.topic")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentFailedTopic() {
        return TopicBuilder.name("payment.failed.topic")
                .partitions(1)
                .replicas(1)
                .build();
    }
}

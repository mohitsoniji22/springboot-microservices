package com.example.os.api.event;

import com.example.os.api.config.*;
import com.example.os.api.dto.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.kafka.core.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {

    private final KafkaTemplate<String, BaseEvent> kafkaTemplate;
    private final KafkaTopicProperties topicProperties;

    public void publish(BaseEvent event) {

        String topic = resolveTopic(event.eventType);

        kafkaTemplate.send(
                topic,
                event.eventId,
                event
        );

        log.info("Published event={} to topic={}",
                event.eventType, topic);
    }

    private String resolveTopic(EventType type) {
        return switch (type) {
            case PAYMENT_SUCCESS -> topicProperties.getPaymentSuccess();
            case PAYMENT_FAILED -> topicProperties.getPaymentFailed();
        };
    }
}


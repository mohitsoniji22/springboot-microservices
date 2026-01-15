package com.example.ns.api.consumer;

import com.example.ns.api.event.*;
import com.example.ns.api.service.NotificationFacade;
import com.example.ns.api.util.CorrelationIdUtil;
import com.fasterxml.jackson.databind.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentFailedConsumer {

    private final ObjectMapper objectMapper;
    private final NotificationFacade notificationFacade;

    @KafkaListener(
            topics = "${spring.kafka.topics.payment-failed}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(BaseEvent event) {

        PaymentFailedEvent pfe =
                objectMapper.convertValue(event, PaymentFailedEvent.class);

        CorrelationIdUtil.set(pfe.getCorrelationId());
        log.info("Received PAYMENT_FAILED event for order {} with correlationId {}",
                pfe.getOrderId(), pfe.getCorrelationId());

        try {
            notificationFacade.handlePaymentFailure(pfe);
        } catch (Exception e) {
            log.error("Error handling PAYMENT_SUCCESS for order {}", pfe.getOrderId(), e);
            // Optional: send to dead-letter topic
        }
    }
}

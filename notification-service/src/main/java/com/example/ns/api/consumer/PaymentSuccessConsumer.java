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
public class PaymentSuccessConsumer {

    private final ObjectMapper objectMapper;
    private final NotificationFacade notificationFacade;

    @KafkaListener(
            topics = "${spring.kafka.topics.payment-success}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(BaseEvent event) {

        PaymentSuccessEvent pse =
                objectMapper.convertValue(event, PaymentSuccessEvent.class);

        CorrelationIdUtil.set(pse.getCorrelationId());
        log.info("Received PAYMENT_SUCCESS event for order {} with correlationId {}",
                pse.getOrderId(), pse.getCorrelationId());

        try {
            notificationFacade.handlePaymentSuccess(pse);
        } catch (Exception e) {
            log.error("Error handling PAYMENT_SUCCESS for order {}", pse.getOrderId(), e);
            // Optional: send to dead-letter topic
        }
    }
}

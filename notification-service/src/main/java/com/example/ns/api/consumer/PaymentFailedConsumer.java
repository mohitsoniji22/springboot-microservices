package com.example.ns.api.consumer;

import com.example.ns.api.event.*;
import com.example.ns.api.service.*;
import com.example.ns.api.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.kafka.annotation.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentFailedConsumer {

    private final NotificationFacade notificationFacade;

    @KafkaListener(topics = "payment.failed", groupId = "notification-group")
    public void consume(PaymentFailedEvent event) {

        CorrelationIdUtil.set(event.getCorrelationId());
        log.info("Received PAYMENT_FAILED event for order {}", event.getOrderId());

        notificationFacade.handlePaymentFailure(event);
    }
}
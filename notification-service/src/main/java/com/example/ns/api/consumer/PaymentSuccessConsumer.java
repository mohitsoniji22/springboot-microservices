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
public class PaymentSuccessConsumer {

    private final NotificationFacade notificationFacade;

    @KafkaListener(topics = "payment.success", groupId = "notification-group")
    public void consume(PaymentSuccessEvent event) {

        CorrelationIdUtil.set(event.getCorrelationId());
        log.info("Received PAYMENT_SUCCESS event for order {}", event.getOrderId());

        notificationFacade.handlePaymentSuccess(event);
    }
}

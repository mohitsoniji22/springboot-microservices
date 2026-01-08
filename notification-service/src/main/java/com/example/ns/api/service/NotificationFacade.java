package com.example.ns.api.service;

import com.example.ns.api.domain.*;
import com.example.ns.api.event.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationFacade {

    private final NotificationDispatcher dispatcher;

    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        NotificationMessage message = NotificationMessage.builder()
                .userId(event.getUserId())
                .subject("Payment Successful")
                .body("Your payment of ₹" + event.getAmount() +
                        " for order " + event.getOrderId() + " was successful.")
                .type(NotificationType.EMAIL)
                .build();

        dispatcher.dispatch(message);
    }

    public void handlePaymentFailure(PaymentFailedEvent event) {
        NotificationMessage message = NotificationMessage.builder()
                .userId(event.getUserId())
                .email(event.getEmail())
                .subject("Payment Failed")
                .body("Payment failed for order " + event.getOrderId()
                        + ". Reason: " + event.getReason())
                .type(NotificationType.EMAIL)
                .build();

        dispatcher.dispatch(message);
    }
}
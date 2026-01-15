package com.example.os.api.event;

import com.example.os.api.dto.EventType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentFailedEvent extends BaseEvent {

    private String reason;

    public PaymentFailedEvent(
            Long orderId,
            String username,
            BigDecimal amount,
            String email,
            String correlationId,
            String reason
    ) {
        super(orderId, username, amount, email, correlationId, EventType.PAYMENT_FAILED);
        this.reason = reason;
    }
}

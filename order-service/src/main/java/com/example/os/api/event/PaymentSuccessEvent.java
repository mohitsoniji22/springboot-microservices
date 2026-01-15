package com.example.os.api.event;

import com.example.os.api.dto.EventType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentSuccessEvent extends BaseEvent {

    private Long paymentId;

    public PaymentSuccessEvent(
            Long orderId,
            String username,
            BigDecimal amount,
            String email,
            String correlationId,
            Long paymentId
    ) {
        super(orderId, username, amount, email, correlationId, EventType.PAYMENT_SUCCESS);
        this.paymentId = paymentId;
    }
}

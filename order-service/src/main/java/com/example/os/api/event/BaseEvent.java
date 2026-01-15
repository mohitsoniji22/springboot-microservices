package com.example.os.api.event;

import com.example.os.api.dto.EventType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseEvent {

    protected String eventId;
    protected String correlationId;
    protected Instant occurredAt;
    protected EventType eventType;

    protected Long orderId;
    protected String username;
    protected BigDecimal amount;
    protected String email;

    protected BaseEvent(
            Long orderId,
            String username,
            BigDecimal amount,
            String email,
            String correlationId,
            EventType eventType
    ) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = Instant.now();
        this.orderId = orderId;
        this.username = username;
        this.amount = amount;
        this.email = email;
        this.correlationId = correlationId;
        this.eventType = eventType;
    }
}

package com.example.ns.api.event;

import com.example.ns.api.domain.*;
import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.math.*;
import java.time.*;
import java.util.*;

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

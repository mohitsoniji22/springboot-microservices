package com.example.ns.api.event;

import lombok.*;

import java.time.*;

@Data
public abstract class DomainEvent {
    public String eventId;
    public String correlationId;
    public Instant occurredAt;
}

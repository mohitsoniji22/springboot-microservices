package com.example.ns.api.event;

import lombok.*;

@Data
public class PaymentFailedEvent extends DomainEvent {

    public String orderId;
    public String userId;
    public  String email;
    public String reason;
}
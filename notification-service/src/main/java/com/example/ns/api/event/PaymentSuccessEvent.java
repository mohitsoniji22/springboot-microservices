package com.example.ns.api.event;

import lombok.*;

import java.math.*;

@Data
public class PaymentSuccessEvent extends DomainEvent {

    public String orderId;
    public String userId;
    public  String email;
    public BigDecimal amount;
}

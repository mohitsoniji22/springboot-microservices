package com.example.os.api.dto;

import java.math.*;

public record Payment(Long paymentId, String paymentStatus, String transactionId, Long orderId, BigDecimal amount) {
}
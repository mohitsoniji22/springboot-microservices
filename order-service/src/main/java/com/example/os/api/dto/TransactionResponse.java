package com.example.os.api.dto;

import com.example.os.api.entity.Order;

public record TransactionResponse(Order order, String transactionId, String message) {
}
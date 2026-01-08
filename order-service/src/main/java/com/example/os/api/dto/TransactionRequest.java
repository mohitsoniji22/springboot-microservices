package com.example.os.api.dto;

import com.example.os.api.entity.Order;

public record TransactionRequest(Order order, Payment payment) {
}
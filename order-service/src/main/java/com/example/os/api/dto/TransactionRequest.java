package com.example.os.api.dto;

import java.math.*;

public record TransactionRequest(String name, int quantity, BigDecimal amount) {
}
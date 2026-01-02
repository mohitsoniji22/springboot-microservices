package com.example.cloud_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping("/fallback/order")
    public ResponseEntity<Map<String, Object>> orderFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "message", "Order Service is currently unavailable",
                        "service", "order-service",
                        "status", 503
                ));
    }

    @GetMapping("/fallback/payment")
    public ResponseEntity<Map<String, Object>> paymentFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "message", "Payment Service is currently unavailable",
                        "service", "payment-service",
                        "status", 503
                ));
    }

    @GetMapping("/fallback/security")
    public ResponseEntity<Map<String, Object>> securityFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "message", "Security Service is currently unavailable",
                        "service", "security-service",
                        "status", 503
                ));
    }
}

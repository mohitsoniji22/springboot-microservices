package com.example.cloud_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.server.reactive.ServerHttpRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@RestController
public class FallbackController {

    @RequestMapping("/fallback/order")
    public ResponseEntity<Map<String, Object>> orderFallback(ServerHttpRequest request) {
        String method = request.getMethod() != null ? request.getMethod().name() : "UNKNOWN";
        log.info("Fallback invoked for order: method={}, path={}", method, request.getURI().getPath());
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .header("X-Fallback-Invoked", "true")
                .body(Map.of(
                        "message", "Order Service is currently unavailable",
                        "service", "order-service",
                        "status", 503
                ));
    }

    @RequestMapping("/fallback/payment")
    public ResponseEntity<Map<String, Object>> paymentFallback(ServerHttpRequest request) {
        String method = request.getMethod() != null ? request.getMethod().name() : "UNKNOWN";
        log.info("Fallback invoked for payment: method={}, path={}", method, request.getURI().getPath());
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .header("X-Fallback-Invoked", "true")
                .body(Map.of(
                        "message", "Payment Service is currently unavailable",
                        "service", "payment-service",
                        "status", 503
                ));
    }

    @RequestMapping("/fallback/security")
    public ResponseEntity<Map<String, Object>> securityFallback(ServerHttpRequest request) {
        String method = request.getMethod() != null ? request.getMethod().name() : "UNKNOWN";
        log.info("Fallback invoked for security: method={}, path={}", method, request.getURI().getPath());
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .header("X-Fallback-Invoked", "true")
                .body(Map.of(
                        "message", "Security Service is currently unavailable",
                        "service", "security-service",
                        "status", 503
                ));
    }
}

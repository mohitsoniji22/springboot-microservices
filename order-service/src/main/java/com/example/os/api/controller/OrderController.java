package com.example.os.api.controller;

import com.example.os.api.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import com.example.os.api.common.TransactionRequest;
import com.example.os.api.common.TransactionResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService service;

    public static final String PAYMENT_SERVICE = "paymentService";

    @PostMapping("/bookOrder")
    @CircuitBreaker(name = PAYMENT_SERVICE, fallbackMethod = "bookOrderFallback")
    public TransactionResponse bookOrder(@RequestBody TransactionRequest request) throws JsonProcessingException{
        return service.saveOrder(request);

        // do a rest call to payment and pass the order id
    }

    // Fallback method for bookOrder
    public TransactionResponse bookOrderFallback(TransactionRequest request, Throwable throwable) {
        return new TransactionResponse(null, -1.0, Integer.toString(-1),"Fallback response: Payment OrderService is currently unavailable.");
    }
}
package com.example.os.api.controller;

import com.example.os.api.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.example.os.api.dto.TransactionRequest;
import com.example.os.api.dto.TransactionResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping("/bookOrder")
    public TransactionResponse bookOrder(@RequestBody TransactionRequest request) throws JsonProcessingException{
        return service.placeOrder(request);
    }
}
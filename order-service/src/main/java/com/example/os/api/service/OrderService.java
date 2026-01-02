package com.example.os.api.service;

import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import com.example.os.api.common.Payment;
import com.example.os.api.common.TransactionRequest;
import com.example.os.api.common.TransactionResponse;
import com.example.os.api.entity.Order;
import com.example.os.api.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.reactive.function.client.*;

@Slf4j
@Service
@RefreshScope
public class OrderService {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${microservices.payment-service.endpoints.endpoint.url}")
    private String ENDPOINT_URL;

    public TransactionResponse saveOrder(TransactionRequest request) throws JsonProcessingException {

        Order order = request.getOrder();
        Payment payment = request.getPayment();

        payment.setOrderId(order.getId());
        payment.setAmount(order.getPrice());

        log.info("OrderService Request : {}",
                new ObjectMapper().writeValueAsString(request));

        Payment paymentResponse = webClientBuilder.build()
                .post()
                .uri(ENDPOINT_URL)
                .bodyValue(payment)
                .retrieve()
                .bodyToMono(Payment.class)
                .block(); //

        log.info("PaymentService Response from OrderService : {}",
                new ObjectMapper().writeValueAsString(paymentResponse));

        String response = paymentResponse.getPaymentStatus().equalsIgnoreCase("success")
                ? "payment processing successful and order placed"
                : "there is a failure in payment api , order added to cart";

        repository.save(order);

        return new TransactionResponse(
                order,
                paymentResponse.getAmount(),
                paymentResponse.getTransactionId(),
                response
        );
    }
}
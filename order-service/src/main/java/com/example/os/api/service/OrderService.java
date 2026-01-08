package com.example.os.api.service;

import com.example.os.api.dto.*;
import com.example.os.api.exception.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import com.example.os.api.entity.Order;
import com.example.os.api.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.*;
import org.springframework.transaction.annotation.*;
import org.springframework.web.reactive.function.client.*;

@Slf4j
@Service
@RefreshScope
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final String ENDPOINT_URL;

    public OrderService(OrderRepository orderRepository,
                        WebClient.Builder webClientBuilder,
                        @Value("${microservices.payment-service.endpoints.endpoint.url}") String ENDPOINT_URL) {
        this.orderRepository = orderRepository;
        this.webClientBuilder = webClientBuilder;
        this.ENDPOINT_URL = ENDPOINT_URL;
    }

    public TransactionResponse placeOrder(TransactionRequest request) throws JsonProcessingException {
        log.debug("Order request received: {}",
                new ObjectMapper().writeValueAsString(request));

        Order order = createOrder(request);
        Payment payment = doPayment(order);
        log.debug("PaymentService Response from payment-service : {}",
                new ObjectMapper().writeValueAsString(payment));
        updateOrderStatus(order.getId(), payment);
        String response = payment.paymentStatus().equalsIgnoreCase("success")
                ? "payment processing successful and order placed " + order.getId()
                : "there is a failure in payment api , order added to cart " + order.getId();

        log.info(response);
        return new TransactionResponse(
                order,
                payment.transactionId(),
                response
        );
    }

    @Transactional
    public Order createOrder(TransactionRequest request) {
        Order order = request.order();
        order.setStatus(String.valueOf(Status.PENDING));
        return orderRepository.save(order);
    }

    public Payment doPayment(Order order) {
        if (order.getId() == null) {
            throw new TransactionFailedException("Order ID is null. Cannot process payment.");
        }
        Payment payment = new Payment(
                null,
                null,
                null,
                order.getId(),
                order.getAmount());

        return webClientBuilder.build()
                .post()
                .uri(ENDPOINT_URL)
                .bodyValue(payment)
                .retrieve()
                .bodyToMono(Payment.class)
                .block();
    }

    @Transactional
    public void updateOrderStatus(Long orderId, Payment paymentResponse) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new TransactionFailedException("Order not found in Order table: " + orderId));
        order.setStatus(paymentResponse.paymentStatus());
        orderRepository.save(order);
    }
}
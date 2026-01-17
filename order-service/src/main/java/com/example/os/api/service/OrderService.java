package com.example.os.api.service;

import com.example.os.api.dto.*;
import com.example.os.api.event.*;
import com.example.os.api.exception.*;
import com.example.os.api.security.*;
import com.example.os.api.security.CurrentUser;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;

import com.example.os.api.entity.Order;
import com.example.os.api.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.transaction.annotation.*;
import org.springframework.web.reactive.function.client.*;

import java.net.http.*;
import java.time.*;

@Slf4j
@Service
@RefreshScope
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final PaymentEventPublisher paymentEventPublisher;
    private final String ENDPOINT_URL;
    private final JwtUtil jwtUtil;
    private final Clock clock;

    public OrderService(OrderRepository orderRepository,
                        JwtUtil jwtUtil,
                        WebClient.Builder webClientBuilder,
                        PaymentEventPublisher paymentEventPublisher,
                        @Value("${microservices.payment-service.endpoints.endpoint.url}") String ENDPOINT_URL) {
        this.orderRepository = orderRepository;
        this.jwtUtil = jwtUtil;
        this.webClientBuilder = webClientBuilder;
        this.paymentEventPublisher = paymentEventPublisher;
        this.ENDPOINT_URL = ENDPOINT_URL;
        this.clock = Clock.system(ZoneOffset.UTC);
    }

    public TransactionResponse placeOrder(TransactionRequest request) throws JsonProcessingException {
        log.debug("Order request received: {}", request);
        CurrentUser currentUser = jwtUtil.getCurrentUser();
        Order order = createOrder(request, currentUser);
        Payment payment = doPayment(order);
        order = updateOrderStatus(order.getId(), payment);
        publishOrderEvent(order, currentUser);

        String response = Status.SUCCESS == payment.paymentStatus()
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
    public Order createOrder(TransactionRequest request, CurrentUser currentUser) {
        Order order = new Order();
        order.setQty(request.quantity());
        order.setAmount(request.amount());
        order.setProductName(request.name());
        order.setStatus(Status.PENDING);
        order.setUsername(currentUser.username());
        order.setCreatedAt(clock.instant());
        return orderRepository.save(order);
    }

    public Payment doPayment(Order order) throws JsonProcessingException {
        if (order.getId() == null) {
            throw new TransactionFailedException("Order ID is null. Cannot process payment.");
        }
        Payment payment = new Payment(
                null,
                null,
                null,
                order.getId(),
                order.getAmount());

        Payment paymentResponse = webClientBuilder.build()
                .post()
                .uri(ENDPOINT_URL)
                .header(HttpHeaders.AUTHORIZATION,
                        "Bearer " + jwtUtil.getToken())
                .bodyValue(payment)
                .retrieve()
                .bodyToMono(Payment.class)
                .block();
        log.debug("PaymentService Response : {}", paymentResponse);
        return paymentResponse;
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, Payment paymentResponse) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new TransactionFailedException("Order not found: " + orderId));
        order.setStatus(paymentResponse.paymentStatus());
        order.setPaymentId(paymentResponse.paymentId());
        return orderRepository.save(order);
    }

    private void publishOrderEvent(Order order, CurrentUser currentUser) {
        BaseEvent event = Status.SUCCESS == order.getStatus()
                ? new PaymentSuccessEvent(order.getId(), order.getUsername(), order.getAmount(),
                currentUser.email(), null, order.getPaymentId())
                : new PaymentFailedEvent(order.getId(), order.getUsername(), order.getAmount(),
                currentUser.email(), null, "Payment failed due to some reason");

        paymentEventPublisher.publish(event);
    }
}
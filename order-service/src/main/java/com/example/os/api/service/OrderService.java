package com.example.os.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.os.api.common.Payment;
import com.example.os.api.common.TransactionRequest;
import com.example.os.api.common.TransactionResponse;
import com.example.os.api.entity.Order;
import com.example.os.api.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RefreshScope
public class OrderService {

    @Autowired
    private OrderRepository repository;

    @Autowired
    @Lazy
    private RestTemplate template;

    @Value("${microservices.payment-service.endpoints.endpoint.url}")
    private String ENDPOINT_URL;

    private Logger log = LoggerFactory.getLogger(OrderService.class);

    public TransactionResponse saveOrder(TransactionRequest request) throws JsonProcessingException {
        String response = "";

        Order order = request.getOrder();
        Payment payment = request.getPayment();
        payment.setOrderId(order.getId());
        payment.setAmount(order.getPrice());

        log.info("OrderService Request : {}", new ObjectMapper().writeValueAsString(request));
        // rest call
        Payment paymentResponse = template.postForObject(ENDPOINT_URL, payment, Payment.class);

        log.info("PaymentService Response from OrderService Rest call : {}", new ObjectMapper().writeValueAsString(paymentResponse));

        response = paymentResponse.getPaymentStatus().equals("success") ? "payment processing successful and order placed" : "there is a failure in payment api , order added to cart";

        repository.save(order);
        return new TransactionResponse(order, paymentResponse.getAmount(), paymentResponse.getTransactionId(), response);
    }
}
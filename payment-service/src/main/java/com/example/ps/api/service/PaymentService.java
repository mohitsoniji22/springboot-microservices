package com.example.ps.api.service;

import java.util.*;

import com.example.ps.api.dto.*;
import lombok.extern.slf4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ps.api.entity.Payment;
import com.example.ps.api.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    private static final Random RANDOM = new Random();

    public Payment doPayment(Payment payment) throws JsonProcessingException {
        log.debug("PaymentService request received: {}", new ObjectMapper().writeValueAsString(payment));
        payment.setPaymentStatus(paymentProcessing());
        payment.setTransactionId(UUID.randomUUID().toString());
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment saved: {}", new ObjectMapper().writeValueAsString(savedPayment));
        return savedPayment;
    }

    public Status paymentProcessing() {
        // api should be 3rd party payment gateway (like paypal may be)
        return RANDOM.nextBoolean() ? Status.SUCCESS : Status.FAILURE;
    }

    public List<Payment> findPaymentHistoryByOrderId(int orderId) throws JsonProcessingException {
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        log.debug("findPaymentHistoryByOrderId : {}", new ObjectMapper().writeValueAsString(payments));
        return payments;
    }

}
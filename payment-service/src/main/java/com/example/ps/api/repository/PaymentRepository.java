package com.example.ps.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ps.api.entity.Payment;

import java.util.*;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByOrderId(int orderId);
}
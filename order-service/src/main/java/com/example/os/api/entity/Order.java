package com.example.os.api.entity;

import com.example.os.api.dto.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.*;
import java.time.*;

@Entity
@Table(name = "ORDER_TB")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int qty;

    private String productName;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String username;

    private Long paymentId;

    private Instant createdAt;
}
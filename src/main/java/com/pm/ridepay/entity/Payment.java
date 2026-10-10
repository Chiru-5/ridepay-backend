package com.pm.ridepay.entity;


import com.pm.ridepay.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id", nullable = true)
    private Ride ride;
//    nullable---> unmatched payments have no ride yet

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;


    @Column(nullable = false, unique = true, length = 100)
    private String gatewayTransactionId;
//    This is the id that gateway provides
//    The idempotency key -- must be unique never be duplicated


    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;


    @Column(length = 100)
    private String payerUpiId;
//    It can be nullable because the gateway always not provide this

    @Column(length = 100)
    private String payerName;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus paymentStatus;


    @Column(nullable = false)
    private LocalDateTime receivedAt;

}

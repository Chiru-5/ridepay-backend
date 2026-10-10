package com.pm.ridepay.entity;


import com.pm.ridepay.enums.RideStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rides")
@Getter
@Setter
public class Ride {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;


    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal farePerPerson;

    @Column(nullable = false)
    private Integer passengerCount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal targetAmount;


    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal collectedAmount;
//    by default 0.00 at creation time

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RideStatus status;


    @Column(nullable = false, updatable = false)
    private LocalDateTime startedAt;


    private LocalDateTime completedAt;


}

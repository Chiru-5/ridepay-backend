package com.pm.ridepay.entity;


import com.pm.ridepay.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "drivers")
@Getter
@Setter
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true)
    private String phoneNumber;
    // Phone number is stored and this is used to send the OTP and OTP login


    @Column(nullable = false, length = 20)
    private String bankAccountNumber;


    @Column(nullable = false, length = 11)
    private String ifscCode;


    @Column(length = 20)
    private String vehicleNumber;


    @Column(unique = true, length = 100)
    private String razorpayQrId;
//    This is the reference to the razorpay linked account
//    With this only we will link the account through KYC record

    @Column(nullable = false)
    private Boolean isActive;
// This field is for does the driver account is still active or not

    @Column(nullable = false, length = 100)
    private String upiId;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VerificationStatus verificationStatus;


    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    @Column(length = 500)
    private String qrImageUrl;
}

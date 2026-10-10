package com.pm.ridepay.repository;


import com.pm.ridepay.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    Optional<Driver> findByPhoneNumber(String phoneNumber);
//    when a driver tries to log in, you look him up by phone number to check if he exists before sending/ verifying an OTP
//    This we will be using in AuthService for verifying the OTP
}

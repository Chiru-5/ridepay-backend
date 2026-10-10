package com.pm.ridepay.repository;

import com.pm.ridepay.entity.Ride;
import com.pm.ridepay.enums.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    Optional<Ride> findByDriverIdAndStatus(Long driverId, RideStatus status);
//    This is used to ensure that every driver has only one active ride
//    We use this in RideService enforces only one ACTIVE ride per driver
//    We will check this before allowing a new "start ride" call
//   And the PaymentMatchingService finds which ride a new payment should count toward


    List<Ride> findByDriverIdOrderByStartedAtDesc(Long driverId);
//    This covers the ride history/ day summary screen from UI flow
}

package com.pm.ridepay.repository;

import com.pm.ridepay.entity.Driver;
import com.pm.ridepay.entity.Ride;
import com.pm.ridepay.enums.RideStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    Long driver(Driver driver);
//    This covers the ride history/ day summary screen from UI flow



    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Ride r where r.driver.id= :driverId and r.status= :status")
    Optional<Ride> findByDriverIdAndStatusForUpdate(@Param("driverId") Long driverId, @Param("status") RideStatus status);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Ride r where r.id = :id")
    Optional<Ride> findByIdForUpdate(@Param("id") Long id);

    @Query("select r.id from Ride r where r.driver.id = :driverId and r.status = :status")
    Optional<Long> findActiveRideId(@Param("driverId") Long driverId,
                                    @Param("status") RideStatus status);
}

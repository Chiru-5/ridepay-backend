package com.pm.ridepay.repository;


import com.pm.ridepay.entity.Driver;
import com.pm.ridepay.entity.Payment;
import com.pm.ridepay.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);
//    This is our idempotency check. Before processing  any webhook, we query this first.
//    If this returns something then we've already handled this exact transaction, so we will stop immediately
//    Why we are checking sometime the gateway may provide duplicates so we have to check so that there are no duplicate payments


    List<Payment> findByDriverIdAndPaymentStatus(Long driverId, PaymentStatus paymentStatus);
//    It retrieves the unmatched payments list (PENDING_UNASSIGNED) for the drivers "assign this payment" screen

    List<Payment> findByRideId(Long rideId);

    Long driver(Driver driver);
//    It gets the every payment belonging to one ride ---> used to display the live running list on the tracker screen

}

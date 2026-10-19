package com.pm.ridepay.service;


import com.pm.ridepay.entity.Driver;
import com.pm.ridepay.entity.Payment;
import com.pm.ridepay.entity.Ride;
import com.pm.ridepay.enums.PaymentStatus;
import com.pm.ridepay.enums.RideStatus;
import com.pm.ridepay.repository.PaymentRepository;
import com.pm.ridepay.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentMatchingService {

    private final RideRepository rideRepository;
    private final PaymentRepository paymentRepository;
    private final RideService rideService;


    public void handleIncomingPayment(Driver driver, String gatewayTransactionId, BigDecimal amount,
                                      String payerUpiId, String payerName){


//     Idempotency check --> Here the request is stopped immediately if this exact transaction was already processed
        if(paymentRepository.findByGatewayTransactionId(gatewayTransactionId).isPresent()){
            return;
        }

        Optional<Ride> activeRide=rideRepository.findByDriverIdAndStatus(driver.getId(), RideStatus.ACTIVE);
        Payment payment=new Payment();
        payment.setDriver(driver);
        payment.setGatewayTransactionId(gatewayTransactionId);
        payment.setAmount(amount);
        payment.setPayerUpiId(payerUpiId);
        payment.setPayerName(payerName);
        payment.setReceivedAt(LocalDateTime.now());



        if(activeRide.isEmpty()){

//            No active ride right now ---> Then mark its as unmatched, driver assigns it later
            payment.setRide(null);
            payment.setPaymentStatus(PaymentStatus.PENDING_UNASSIGNED);
            paymentRepository.save(payment);
            return;
        }

        Ride ride= activeRide.get();
        payment.setRide(ride);
        payment.setPaymentStatus(PaymentStatus.MATCHED);
        paymentRepository.save(payment);

        rideService.applyPaymentAndCheckCompletion(ride, amount);
        rideRepository.save(ride);
    }
}

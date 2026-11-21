package com.pm.ridepay.service;


import com.pm.ridepay.dto.response.PaymentDTO;
import com.pm.ridepay.entity.Driver;
import com.pm.ridepay.entity.Payment;
import com.pm.ridepay.entity.Ride;
import com.pm.ridepay.enums.PaymentStatus;
import com.pm.ridepay.enums.RideStatus;
import com.pm.ridepay.repository.PaymentRepository;
import com.pm.ridepay.repository.RideRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentMatchingService {

    private final RideRepository rideRepository;
    private final PaymentRepository paymentRepository;
    private final RideService rideService;

    @Transactional
    public void handleIncomingPayment(Driver driver, String gatewayTransactionId,
                                      BigDecimal amount, String payerUpiId, String payerName) {

        if (paymentRepository.findByGatewayTransactionId(gatewayTransactionId).isPresent()) {
            return;
        }

        // Step 1: find the id only (a plain read: nothing is locked or cached yet)
        Optional<Long> activeRideId =
                rideRepository.findActiveRideId(driver.getId(), RideStatus.ACTIVE);

        // Step 2: lock that one row by primary key, then re-check it under the lock
        Ride ride = activeRideId
                .flatMap(rideRepository::findByIdForUpdate)
                .filter(r -> r.getStatus() == RideStatus.ACTIVE)
                .orElse(null);

        Payment payment = new Payment();
        payment.setDriver(driver);
        payment.setGatewayTransactionId(gatewayTransactionId);
        payment.setAmount(amount);
        payment.setPayerUpiId(payerUpiId);
        payment.setPayerName(payerName);
        payment.setReceivedAt(LocalDateTime.now());

        if (ride == null) {
            payment.setRide(null);
            payment.setPaymentStatus(PaymentStatus.PENDING_UNASSIGNED);
            paymentRepository.save(payment);
            return;
        }

        payment.setRide(ride);
        payment.setPaymentStatus(PaymentStatus.MATCHED);
        paymentRepository.save(payment);

        rideService.applyPaymentAndCheckCompletion(ride, amount);
        rideRepository.save(ride);
    }


    public List<PaymentDTO> getUnmatchedPayments(Long driverId){
        return paymentRepository.findByDriverIdAndPaymentStatus(driverId, PaymentStatus.PENDING_UNASSIGNED)
                .stream()
                .map(p-> new PaymentDTO(p.getId(), p.getAmount(), p.getPayerUpiId(), p.getReceivedAt()))
                .toList();
    }
}

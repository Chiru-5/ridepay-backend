package com.pm.ridepay.service;


import com.pm.ridepay.dto.request.AdjustRideRequest;
import com.pm.ridepay.dto.request.StartRideRequest;
import com.pm.ridepay.dto.response.PaymentDTO;
import com.pm.ridepay.dto.response.RideStatusResponse;
import com.pm.ridepay.entity.Driver;
import com.pm.ridepay.entity.Payment;
import com.pm.ridepay.entity.Ride;
import com.pm.ridepay.enums.PaymentStatus;
import com.pm.ridepay.enums.RideStatus;
import com.pm.ridepay.enums.VerificationStatus;
import com.pm.ridepay.exception.*;
import com.pm.ridepay.repository.DriverRepository;
import com.pm.ridepay.repository.PaymentRepository;
import com.pm.ridepay.repository.RideRepository;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final PaymentRepository paymentRepository;


    public RideStatusResponse startRide(Long driverId, StartRideRequest request){

//        If the driver is not registered in the app then he can't start the ride.
        Driver driver=driverRepository.findById(driverId)
                .orElseThrow(()->new DriverNotFoundException("Driver Not Found"));

//      If the driver's verification is still pending then a driver can not start a ride
//        That case is handled here.
        if(driver.getVerificationStatus()!= VerificationStatus.VERIFIED){
            throw new DriverNotVerifiedException("Complete onboarding before starting a ride");
        }


//      If there is one active ride then we should not start another ride
//        That is handled here. Here we used method called findByDriverIdAndStaus() here driverId, RideStatus is passed
//        If the ride is already present then the method will throw RideAlreadyActiveException other nothing
        rideRepository.findByDriverIdAndStatus(driverId, RideStatus.ACTIVE)
                .ifPresent(r->{
                    throw new RideAlreadyActiveException("A ride already active");
                });

//  Here we are calculating the target amount that need to receive
//        We need farePerPerson and passengerCount we can ge the both values from the request(StartRideRequest)
        BigDecimal target=request.getFarePerPerson()
                .multiply(BigDecimal.valueOf(request.getPassengerCount()));




        Ride ride=new Ride();
        ride.setDriver(driver);
        ride.setFarePerPerson(request.getFarePerPerson());
        ride.setPassengerCount(request.getPassengerCount());
        ride.setTargetAmount(target);
        ride.setCollectedAmount(BigDecimal.ZERO); //Initially zero then it will be updated when payment receives
        ride.setStatus(RideStatus.ACTIVE);
        ride.setStartedAt(LocalDateTime.now());

        rideRepository.save(ride);

        return toResponse(ride, List.of());
    }

//    Called through controller
    public RideStatusResponse getStatus(Long driverId, Long rideId){
        Ride ride=getOwnedRide(driverId, rideId);
        List<Payment> payments=paymentRepository.findByRideId(rideId);

        return toResponse(ride, payments);
    }

//    Called through controller
    public RideStatusResponse cancelRide(Long driverId, Long rideId){
        Ride ride=getOwnedRide(driverId, rideId);

        if(ride.getStatus()!=RideStatus.ACTIVE){
            throw new InvalidRideStateException("Only an Active ride can be cancelled");
        }

        ride.setStatus(RideStatus.CANCELLED);
        ride.setCompletedAt(LocalDateTime.now());
        rideRepository.save(ride);


        List<Payment> payments=paymentRepository.findByRideId(rideId);
        return toResponse(ride, payments);
    }


//    It handles the two manual-correction paths we designed --> assigning a stray unmatched payments to this ride, or manually adjusting the count up/ down
    public RideStatusResponse adjustRide(Long driverId, Long rideId, AdjustRideRequest request){
        Ride ride=getOwnedRide(driverId, rideId);

        if(ride.getStatus()!=RideStatus.ACTIVE){
            throw new InvalidRideStateException("Only an active ride can be adjusted");
        }

        boolean hasManualAdjustment=request.getManualAdjustment()!= null;
        boolean hasAssignPayment=request.getAssignPaymentId()!= null;

        if(hasManualAdjustment==hasAssignPayment){
            throw new InvalidRideStateException("Provide exactly one of manualAdjustment or assignPaymentId");
        }

        if(hasAssignPayment){
            assignPendingPayment(driverId, ride, request.getAssignPaymentId());
        }
        else{
            applyManualAdjustment(ride, request.getManualAdjustment());
        }

        rideRepository.save(ride);


        List<Payment> payments=paymentRepository.findByRideId(rideId);
        return toResponse(ride, payments);
    }

// This method is used to for: has this ride collected enough money yet?/ --> adds an amount to the running total, and flips the ride to COMPLETED if the target's been reached
    public void applyPaymentAndCheckCompletion(Ride ride, BigDecimal amount){
        ride.setCollectedAmount(ride.getCollectedAmount().add(amount));


        if(ride.getCollectedAmount().compareTo(ride.getTargetAmount())>=0){
            ride.setStatus(RideStatus.COMPLETED);
            ride.setCompletedAt(LocalDateTime.now());
        }
    }


    private void assignPendingPayment(Long driverId, Ride ride, Long paymentId){
        Payment payment=paymentRepository.findById(paymentId)
                .orElseThrow(()-> new PaymentNotFoundException("Payment not found"));

        if(!payment.getDriver().getId().equals(driverId)){
            throw new PaymentNotFoundException("Payment not found");
        }

        if(payment.getPaymentStatus()!= PaymentStatus.PENDING_UNASSIGNED){
            throw new InvalidRideStateException("This payment is already assigned");
        }

        payment.setRide(ride);
        payment.setPaymentStatus(PaymentStatus.MATCHED);
        paymentRepository.save(payment);

        applyPaymentAndCheckCompletion(ride, payment.getAmount());
    }

    private void applyManualAdjustment(Ride ride, Integer adjustment){
        BigDecimal delta=ride.getFarePerPerson().multiply(BigDecimal.valueOf(adjustment));
        BigDecimal newTotal=ride.getCollectedAmount().add(delta);

        if(newTotal.compareTo(BigDecimal.ZERO)<0){
            throw new InvalidRideStateException("Adjustment would make collected amount negative");
        }

        ride.setCollectedAmount(newTotal);

        if(newTotal.compareTo(ride.getTargetAmount())>=0){
            ride.setStatus(RideStatus.COMPLETED);
            ride.setCompletedAt(LocalDateTime.now());
        }
    }


    private Ride getOwnedRide(Long driverId, Long rideId){
        Ride ride=rideRepository.findById(rideId)
                .orElseThrow(()-> new RideNotFoundException("Ride not Found"));

        if(!ride.getDriver().getId().equals(driverId)){
            throw new RideNotFoundException("Ride not Found");
        }

        return ride;
    }


    private RideStatusResponse toResponse(Ride ride, List<Payment> payments){
        List<PaymentDTO> paymentDTOS=payments.stream()
                .map(payment -> new PaymentDTO(payment.getId(),
                        payment.getAmount(),
                        payment.getPayerUpiId(),
                        payment.getReceivedAt()))
                .toList();


        int estimatedPaid=ride.getCollectedAmount()
                .divide(ride.getFarePerPerson(), RoundingMode.DOWN)
                .intValue();


        RideStatusResponse response=new RideStatusResponse();
        response.setRideId(ride.getId());
        response.setTargetAmount(ride.getTargetAmount());
        response.setCollectedAmount(ride.getCollectedAmount());
        response.setEstimatedPaid(estimatedPaid);
        response.setPassengerCount(ride.getPassengerCount());
        response.setStatus(ride.getStatus());
        response.setPayments(paymentDTOS);

        return response;
    }


}

package com.pm.ridepay.service;


import com.pm.ridepay.dto.request.StartRideRequest;
import com.pm.ridepay.dto.response.PaymentDTO;
import com.pm.ridepay.dto.response.RideStatusResponse;
import com.pm.ridepay.entity.Driver;
import com.pm.ridepay.entity.Payment;
import com.pm.ridepay.entity.Ride;
import com.pm.ridepay.enums.RideStatus;
import com.pm.ridepay.enums.VerificationStatus;
import com.pm.ridepay.exception.*;
import com.pm.ridepay.repository.DriverRepository;
import com.pm.ridepay.repository.PaymentRepository;
import com.pm.ridepay.repository.RideRepository;
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


    public RideStatusResponse getStatus(Long driverId, Long rideId){
        Ride ride=getOwnedRide(driverId, rideId);
        List<Payment> payments=paymentRepository.findByRideId(rideId);

        return toresponse(ride, payments);
    }

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

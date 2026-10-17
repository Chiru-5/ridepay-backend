package com.pm.ridepay.controller;



import com.pm.ridepay.dto.request.StartRideRequest;
import com.pm.ridepay.dto.response.RideStatusResponse;
import com.pm.ridepay.service.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rides")
@RequiredArgsConstructor
public class RideController {


    private final RideService rideService;

    @PostMapping("/start")
    public ResponseEntity<RideStatusResponse> startRide(@Valid @RequestBody StartRideRequest request){
        Long driverId=getAuthenticatedDriverId();
        RideStatusResponse response=rideService.startRide(driverId, request);
        return ResponseEntity.ok(response);
    }



    @GetMapping("/{rideId}/status")
    public ResponseEntity<RideStatusResponse> getStatus(@PathVariable Long rideId){
        Long driverId=getAuthenticatedDriverId();
        RideStatusResponse response=rideService.getStatus(driverId, rideId);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/{rideId}/cancel")
    public ResponseEntity<RideStatusResponse> cancelRide(@PathVariable Long rideId){
        Long driverId=getAuthenticatedDriverId();
        RideStatusResponse response=rideService.cancelRide(driverId, rideId);
        return ResponseEntity.ok(response);
    }

    private Long getAuthenticatedDriverId(){
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}

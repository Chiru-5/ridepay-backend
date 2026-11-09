package com.pm.ridepay.controller;


import com.pm.ridepay.dto.request.OnboardRequest;
import com.pm.ridepay.dto.response.DriverStatusResponse;
import com.pm.ridepay.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

//    Onboarding the driver request
    @PostMapping("/onboard")
    public ResponseEntity<DriverStatusResponse> onboard(@Valid @RequestBody OnboardRequest request){
        Long driverId=getAuthenticatedDriverId();
        return ResponseEntity.ok(driverService.onboard(driverId, request));
    }

//  To get the status of the onboarding process
    @GetMapping("/status")
    public ResponseEntity<DriverStatusResponse> getStatus(){
        Long driverId=getAuthenticatedDriverId();
        return ResponseEntity.ok(driverService.getStatus(driverId));
    }

    private Long getAuthenticatedDriverId(){
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}

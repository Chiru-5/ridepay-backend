package com.pm.ridepay.service;


import com.pm.ridepay.dto.request.OnboardRequest;
import com.pm.ridepay.dto.response.DriverProfileResponse;
import com.pm.ridepay.dto.response.DriverStatusResponse;
import com.pm.ridepay.entity.Driver;
import com.pm.ridepay.enums.VerificationStatus;
import com.pm.ridepay.exception.AlreadyOnboardedException;
import com.pm.ridepay.exception.DriverNotFoundException;
import com.pm.ridepay.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final GatewayService gatewayService;


    public DriverStatusResponse onboard(Long driverId, OnboardRequest request){
        Driver driver =driverRepository.findById(driverId).orElseThrow(()-> new DriverNotFoundException("Driver not found"));



        if(driver.getVerificationStatus()== VerificationStatus.VERIFIED){
            throw new AlreadyOnboardedException("Driver already completed onboarding");
        }

        driver.setName(request.getName());
        driver.setBankAccountNumber(request.getBankAccountNumber());
        driver.setIfscCode(request.getIfscCode());
        driver.setVehicleNumber(request.getVehicleNumber());

        GatewayOnboardResult result=gatewayService.onboardDriver(driverId, request.getName());

        driver.setRazorpayQrId(result.getQrCodeId());
        driver.setQrImageUrl(result.getQrImageUrl());
        driver.setVerificationStatus(VerificationStatus.VERIFIED);

        driverRepository.save(driver);

        return toResponse(driver);

    }


    public DriverStatusResponse getStatus(Long driverId){
        Driver driver=driverRepository.findById(driverId).orElseThrow(()->new DriverNotFoundException("Driver not found"));
        return toResponse(driver);
    }

    private DriverStatusResponse toResponse(Driver driver){
        DriverStatusResponse response=new DriverStatusResponse();
        response.setDriverId(driver.getId());
        response.setName(driver.getName());
        response.setVerificationStatus(driver.getVerificationStatus());
        response.setUpiId(driver.getUpiId());
        response.setIsActive(driver.getIsActive());
        response.setQrImageUrl(driver.getQrImageUrl());

        return response;
    }

//    To get the details of a driver and display on the profile screen
    public DriverProfileResponse getProfile(Long driverId){
        Driver driver=driverRepository.findById(driverId)
                .orElseThrow(()-> new DriverNotFoundException("Driver not found"));

        DriverProfileResponse response=new DriverProfileResponse();
        response.setDriverId(driver.getId());
        response.setName(driver.getName());
        response.setPhoneNumber(driver.getPhoneNumber());
        response.setVehicleNumber(driver.getVehicleNumber());
        response.setMaskedAccountNumber(maskAccountNumber(driver.getBankAccountNumber()));
        response.setIfscCode(driver.getIfscCode());
        response.setVerificationStatus(driver.getVerificationStatus());
        response.setUpiId(driver.getUpiId());
        return response;
    }


//    Used to mask the account number
//    Only last four digits will be shown
    private String maskAccountNumber(String account){
        if(account==null){
            return null;
        }
        if(account.length()<=4){
            return "X".repeat(account.length());
        }
        return "X".repeat(account.length()-4)+account.substring(account.length()-4);
    }


    @Transactional
    public void saveDeviceToken(Long driverId, String token){
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(()-> new DriverNotFoundException("Driver not found"));


        for(Driver other: driverRepository.findByFcmToken(token)){
            if(!other.getId().equals(driverId)){
                other.setFcmToken(null);
                driverRepository.save(other);
            }
        }
        driver.setFcmToken(token);
        driverRepository.save(driver);
    }
}

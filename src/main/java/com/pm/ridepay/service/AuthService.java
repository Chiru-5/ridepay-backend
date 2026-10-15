package com.pm.ridepay.service;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.pm.ridepay.dto.request.FirebaseLoginRequest;
import com.pm.ridepay.dto.response.AuthResponse;
import com.pm.ridepay.entity.Driver;
import com.pm.ridepay.enums.VerificationStatus;
import com.pm.ridepay.exception.DriverDeactivatedException;
import com.pm.ridepay.exception.InvalidTokenException;
import com.pm.ridepay.repository.DriverRepository;
import com.pm.ridepay.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final DriverRepository driverRepository;
    private final JwtTokenProvider jwtTokenProvider;


    public AuthResponse loginWithFirebaseToken(FirebaseLoginRequest request){
        FirebaseToken decodedToken;

        try {
            decodedToken= FirebaseAuth.getInstance()
                    .verifyIdToken(request.getFirebaseIdToken());
        }
        catch (FirebaseAuthException e){
            throw new InvalidTokenException("Invalid or expired Firebase Token");
        }

        String phoneNumber=(String) decodedToken.getClaims().get("phone_number");

        Driver driver=driverRepository.findByPhoneNumber(phoneNumber)
                .orElseGet(()->createNewDriver(phoneNumber));

        if(!driver.getIsActive()){
            throw new DriverDeactivatedException("This account has been deactivated");
        }

        String token=jwtTokenProvider.generateToken(driver.getId());
        return new AuthResponse(token, driver.getId());
    }


    private Driver createNewDriver(String phoneNumber){
        Driver driver=new Driver();
        driver.setPhoneNumber(phoneNumber);
        driver.setVerificationStatus(VerificationStatus.PENDING);
        return driverRepository.save(driver);
    }
}

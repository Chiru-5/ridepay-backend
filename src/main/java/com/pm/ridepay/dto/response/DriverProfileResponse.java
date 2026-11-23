package com.pm.ridepay.dto.response;


import com.pm.ridepay.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverProfileResponse {

    private Long driverId;
    private String name;
    private String phoneNumber;
    private String vehicleNumber;
    private String maskedAccountNumber;
    private String ifscCode;
    private VerificationStatus verificationStatus;
    private String upiId;
}

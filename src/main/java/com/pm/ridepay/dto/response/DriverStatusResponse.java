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
public class DriverStatusResponse {


    private Long driverId;
    private String name;
    private VerificationStatus verificationStatus;
    private String upiId;
    private Boolean isActive;
}

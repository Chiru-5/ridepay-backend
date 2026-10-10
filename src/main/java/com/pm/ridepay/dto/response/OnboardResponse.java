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
public class OnboardResponse {


    private Long driverId;
    private VerificationStatus verificationStatus;
}

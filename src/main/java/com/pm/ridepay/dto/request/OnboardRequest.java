package com.pm.ridepay.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OnboardRequest {


    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 20)
    private String bankAccountNumber;


    @NotBlank
    @Size(min = 11, max = 11)
    private String ifscCode;


    @Size(max = 20)
    private String vehicleNumber;

}

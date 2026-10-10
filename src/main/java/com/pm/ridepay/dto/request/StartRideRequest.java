package com.pm.ridepay.dto.request;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StartRideRequest {


    @NotNull
    @DecimalMin(value = "1.0")
    private BigDecimal farePerPerson;


    @NotNull
    @Min(1)
    private Integer passengerCount;
}

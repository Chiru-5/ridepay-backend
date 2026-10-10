package com.pm.ridepay.dto.response;


import com.pm.ridepay.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RideStatusResponse {

    private Long rideId;
    private BigDecimal targetAmount;
    private BigDecimal collectedAmount;
    private int estimatedPaid;
//    For this there is no DB column we will compute this at service layer
    private int passengerCount;
    private RideStatus status;
    private List<PaymentDTO> payments;
}

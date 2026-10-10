package com.pm.ridepay.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdjustRideRequest {


    private Integer manualAdjustment;
    // nullable — +1 or -1 manual correction


    private Long assignPaymentId;
    // nullable — assigns a PENDING_UNASSIGNED payment to this ride
}

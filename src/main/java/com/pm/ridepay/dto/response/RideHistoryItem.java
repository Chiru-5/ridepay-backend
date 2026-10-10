package com.pm.ridepay.dto.response;


import com.pm.ridepay.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RideHistoryItem {


    private Long rideId;
    private BigDecimal targetAmount;
    private BigDecimal collectedAmount;
    private RideStatus status;
    private LocalDateTime startedAt;
}

package com.pm.ridepay.dto.response;


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
public class PaymentDTO {


    private Long id;
    private BigDecimal amount;
    private String payerUpiId;
    private LocalDateTime receivedAt;
}

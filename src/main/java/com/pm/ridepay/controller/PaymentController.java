package com.pm.ridepay.controller;

import com.pm.ridepay.dto.response.PaymentDTO;
import com.pm.ridepay.service.PaymentMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentMatchingService paymentMatchingService;

    public ResponseEntity<List<PaymentDTO>> getUnmatchedPayments(){
        Long driverId=(Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return ResponseEntity.ok(paymentMatchingService.getUnmatchedPayments(driverId));
    }
}

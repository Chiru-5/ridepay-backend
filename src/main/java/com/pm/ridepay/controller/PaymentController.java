package com.pm.ridepay.controller;

import com.pm.ridepay.dto.response.PaymentDTO;
import com.pm.ridepay.service.PaymentMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentMatchingService paymentMatchingService;


    @GetMapping("/unmatched")
    public ResponseEntity<List<PaymentDTO>> getUnmatchedPayments(){
        Long driverId=(Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return ResponseEntity.ok(paymentMatchingService.getUnmatchedPayments(driverId));
    }


    @PostMapping("/{paymentId}/ignore")
    public ResponseEntity<PaymentDTO> ignorePayment(@PathVariable Long paymentId){
        Long driverId = (Long) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return ResponseEntity.ok(paymentMatchingService.ignorePayment(driverId, paymentId));
    }
}

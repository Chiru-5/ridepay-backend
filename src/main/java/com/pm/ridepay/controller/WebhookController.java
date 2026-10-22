package com.pm.ridepay.controller;


import com.pm.ridepay.repository.DriverRepository;
import com.pm.ridepay.service.GatewayService;
import com.pm.ridepay.service.PaymentMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final GatewayService gatewayService;
    private final PaymentMatchingService paymentMatchingService;


    @PostMapping("/payment")
    public ResponseEntity<String> handlePaymentWebhook(@RequestBody String rawPayload, @RequestHeader("X- Razorpay-Signature") String signature){
        if(!gatewayService.verifyWebhookSignature(rawPayload, signature)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Signature");
        }

//        This section is still pending to build. Field extraction + call paymentMatchingService.handleIncomingPayment()
        return ResponseEntity.ok("OK");
    }
}

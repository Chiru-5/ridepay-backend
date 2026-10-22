package com.pm.ridepay.controller;


import com.pm.ridepay.entity.Driver;
import com.pm.ridepay.repository.DriverRepository;
import com.pm.ridepay.service.GatewayService;
import com.pm.ridepay.service.PaymentMatchingService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final GatewayService gatewayService;
    private final PaymentMatchingService paymentMatchingService;
    private final DriverRepository driverRepository;


    @PostMapping("/payment")
    public ResponseEntity<String> handlePaymentWebhook(@RequestBody String rawPayload, @RequestHeader("X- Razorpay-Signature") String signature){
        if(!gatewayService.verifyWebhookSignature(rawPayload, signature)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Signature");
        }


        JSONObject json=new JSONObject(rawPayload);
        String event=json.getString("event");

        if(!"qr_code.credited".equals(event)){
            return ResponseEntity.ok("Ignored - not a payment event");
        }

        JSONObject payload=json.getJSONObject("payload");
        JSONObject paymentEntity=payload.getJSONObject("payment").getJSONObject("entity");
        JSONObject qrEntity=payload.getJSONObject("qr_code").getJSONObject("entity");


        String gatewayTransactionId=paymentEntity.getString("id");
        long amountInPaise=paymentEntity.getLong("amount");
        BigDecimal amount=BigDecimal.valueOf(amountInPaise).divide(BigDecimal.valueOf(100));
        String payerUpiId=paymentEntity.optString("vpa", null);
        String payerName=paymentEntity.optString("email", null);
        String qrCodeId= qrEntity.getString("id");


        Optional<Driver> driverOpt=driverRepository.findByRazorpayQrId(qrCodeId);

        if(driverOpt.isEmpty()){
//            Usually this should not happen because every driver will get the qrId
//            Just logging it for future investigation if anything happens
            return ResponseEntity.ok("Driver not found for this code");
        }

        paymentMatchingService.handleIncomingPayment(
                driverOpt.get(), gatewayTransactionId, amount, payerUpiId, payerName
        );

        return ResponseEntity.ok("Processed");
    }
}

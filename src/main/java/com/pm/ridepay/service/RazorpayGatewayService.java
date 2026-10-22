package com.pm.ridepay.service;


import com.pm.ridepay.exception.GatewayOnboardException;
import com.razorpay.QrCode;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@Service
public class RazorpayGatewayService implements GatewayService{


    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;


    @Value("${razorpay.webhook-secret}")
    private String webhookSecret;

    @Override
    public GatewayOnboardResult onboardDriver(Long driverId, String driverName){
        try{
            RazorpayClient razorpay=new RazorpayClient(keyId, keySecret);
            JSONObject request=new JSONObject();
            request.put("type", "upi_qr");
            request.put("name", "RidePay - "+driverName);
            request.put("usage","multiple_use");
            request.put("fixed_amount", false);

            JSONObject notes=new JSONObject();
            notes.put("driverId", String.valueOf(driverId));
            request.put("notes", notes);


            QrCode qrCode=razorpay.qrCode.create(request);

            return new GatewayOnboardResult(
                    qrCode.get("id"),
                    qrCode.get("image_url")
            );
        }
        catch (Exception e){
            throw new GatewayOnboardException("Failed to create payment QR: "+e.getMessage());
        }
    }

    @Override
    public boolean verifyWebhookSignature(String payload, String receivedSignature) {
        try{
            Mac mac=Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] computedHash=mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String computedSignature= HexFormat.of().formatHex(computedHash);

            return computedSignature.equals(receivedSignature);
        }
        catch (Exception e){
            return false;
        }
    }


}

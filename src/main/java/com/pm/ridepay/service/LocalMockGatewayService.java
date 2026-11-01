package com.pm.ridepay.service;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.pm.ridepay.exception.GatewayOnboardingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.UUID;

@Service
@Profile("local")  /* This is the thing we can be swappable. For now we are testing using local profile if we use Razorpay profile then everything is different but o need to change the code or anything
just we have change the  profile in application.properties to Razorpay that's it. This is the concept of spring profiles */
@RequiredArgsConstructor
public class LocalMockGatewayService implements  GatewayService{


    private static final String LOCAL_SHARED_SECRET="local-dev-shared-secret";

    private final QrImageStore qrImageStore;


    @Override
    public GatewayOnboardResult onboardDriver(Long driverId, String driverName){
        String qrCodeId="qr_mock_"+ UUID.randomUUID().toString().substring(0, 14);


        try{
            String upiString = "upi://pay?pa=yourtestvpa@oksbi&pn="
                    + URLEncoder.encode(driverName, StandardCharsets.UTF_8)
                    +"&cu=INR";

            QRCodeWriter writer=new QRCodeWriter();
            BitMatrix matrix=writer.encode(upiString, BarcodeFormat.QR_CODE, 300, 300);

            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);


            String imageUrl=qrImageStore.store(qrCodeId, outputStream.toByteArray());

            return new GatewayOnboardResult(qrCodeId, imageUrl);
        }
        catch (Exception e){
            throw new GatewayOnboardingException("Failed to generate mook QR"+ e.getMessage());
        }
    }



    @Override
    public boolean verifyWebhookSignature(String payload, String receivedSignature){
        try{
            Mac mac=Mac.getInstance("HmacSHA256");
                mac.init(new SecretKeySpec(LOCAL_SHARED_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
                byte[] computedHash=mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
                return HexFormat.of().formatHex(computedHash).equals(receivedSignature);
        }
        catch (Exception e){
            return false;
        }
    }
}

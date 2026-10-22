package com.pm.ridepay.service;

public interface GatewayService {
    GatewayOnboardResult onboardDriver(Long driverId, String driverName);
    boolean verifyWebhookSignature(String payload, String receivedSignature);
}

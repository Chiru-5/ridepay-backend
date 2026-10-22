package com.pm.ridepay.service;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GatewayOnboardResult {

    private final String qrCodeId;
    private final String qrImageUrl;


}

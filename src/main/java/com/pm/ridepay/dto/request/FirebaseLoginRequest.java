package com.pm.ridepay.dto.request;


import jakarta.validation.constraints.NotBlank;

@NotBlank
public class FirebaseLoginRequest {

    private String firebaseIdToken;
}

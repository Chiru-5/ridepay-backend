package com.pm.ridepay.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NotBlank
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FirebaseLoginRequest {

    private String firebaseIdToken;
}

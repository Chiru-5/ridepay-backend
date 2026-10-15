package com.pm.ridepay.controller;


import com.pm.ridepay.dto.request.FirebaseLoginRequest;
import com.pm.ridepay.dto.response.AuthResponse;
import com.pm.ridepay.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthService authService;

    public ResponseEntity<AuthResponse> login(@Valid @RequestBody FirebaseLoginRequest request){
        AuthResponse response=authService.loginWithFirebaseToken(request);
        return ResponseEntity.ok(response);
    }
}

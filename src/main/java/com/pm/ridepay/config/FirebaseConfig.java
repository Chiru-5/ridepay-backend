package com.pm.ridepay.config;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.google.firebase.FirebaseApp;

import java.io.FileInputStream;
import java.io.IOException;

@Component
public class FirebaseConfig {

    @Value("${firebase.service-account-path}")
    private String serviceAccountPath;


    @PostConstruct
    public void initialize() throws IOException{
        if(FirebaseApp.getApps().isEmpty()){
            FileInputStream serviceAccount=new FileInputStream(serviceAccountPath);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
        }
    }
}

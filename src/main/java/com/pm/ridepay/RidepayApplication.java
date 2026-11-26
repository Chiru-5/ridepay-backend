package com.pm.ridepay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RidepayApplication {

    public static void main(String[] args) {
        SpringApplication.run(RidepayApplication.class, args);
    }

}

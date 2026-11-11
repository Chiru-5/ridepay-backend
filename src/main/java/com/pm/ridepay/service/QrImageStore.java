package com.pm.ridepay.service;


import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class QrImageStore {
// Used concurrent hashmap is used. Because multiple requests could read/ write this map at the same time(different drivers onboarding simultaneously).

    private final Map<String, byte[]> images=new ConcurrentHashMap<>();


    public String store(String id, byte[] pngBytes){
        images.put(id, pngBytes);
        return "/qr-images/"+ id;
    }

    public byte[] get(String id){
        return images.get(id);
    }
}
//The whole class is deliberately simple, in-memory, local-testing-only-stand-in.
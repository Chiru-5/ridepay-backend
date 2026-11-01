package com.pm.ridepay.controller;

import com.pm.ridepay.service.QrImageStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;




// What it does??? ---> A plain GET endpoint that given an ID returns the raw PNG bytes with the correct content type.
//This is what the image loading library(on android) expects when fetching an image from an URL
@RestController
@RequestMapping("/qr-images")
@RequiredArgsConstructor
public class QrImageController {

    private final QrImageStore qrImageStore;


    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable String id){
        byte[] image=qrImageStore.get(id);

        if(image==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(image);
    }
}

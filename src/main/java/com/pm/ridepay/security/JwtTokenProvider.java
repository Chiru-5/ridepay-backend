package com.pm.ridepay.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

//    extracting data from the environment variables
    @Value("${jwt.secret}")
    private String secret;


    @Value("${jwt.expiration-ms}")
    private long expirationMs;


//    for signingKey
    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }


//    Generating the token
    public String generateToken(Long driverId){
        Date now=new Date();
        Date expiry=new Date(now.getTime()+expirationMs);


        return Jwts.builder()
                .subject(String.valueOf(driverId))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    public Long getDriverIdFromToken(String token){
        Claims claims=Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }


    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}

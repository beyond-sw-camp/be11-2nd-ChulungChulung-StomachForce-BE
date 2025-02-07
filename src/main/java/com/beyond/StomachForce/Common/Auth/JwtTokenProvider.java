package com.beyond.StomachForce.Common.Auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.secretKey}")
    private String secretKey;

    private Key ENCRYPT_SECRET_KEY;

    @Value("${jwt.expirationRT}")
    private int expirationRT;

    @Value("${jwt.secretKeyRT}")
    private String secretKeyRT;
    private Key ENCRYPT_RT_SECRET_KEY;

    @PostConstruct
    public void init(){
        ENCRYPT_SECRET_KEY = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS512.getJcaName());
        ENCRYPT_RT_SECRET_KEY = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKeyRT), SignatureAlgorithm.HS512.getJcaName());
    }

    public String createToken(String identify,String role){
        Claims claims = Jwts.claims().setSubject(identify);
        claims.put("role",role);
        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+expiration*60*1000L)) //30분 세팅
                .signWith(ENCRYPT_SECRET_KEY)
                .compact();
        return token;
    }

    public String createRefreshToken(String identify,String role){
        Claims claims = Jwts.claims().setSubject(identify);
        claims.put("role",role);
        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+expirationRT*60*1000L)) //30분 세팅
                .signWith(ENCRYPT_RT_SECRET_KEY)
                .compact();
        return token;
    }
}

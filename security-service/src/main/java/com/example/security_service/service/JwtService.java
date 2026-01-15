package com.example.security_service.service;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import com.example.security_service.entity.*;
import com.example.security_service.exception.*;
import com.example.security_service.repository.*;
import lombok.extern.slf4j.*;
import org.apache.hc.client5.http.auth.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private final UserRepository userRepository;

    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateToken(final String token) {
        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parse(token);
    }

    public String generateToken(String username) {
        User user = userRepository.findByUsername(username).get();
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("userId", user.getUserId());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new java.util.Date(System.currentTimeMillis()))
                .setExpiration(new java.util.Date(System.currentTimeMillis() + 1000 * 60 * 20)) // 20 minutes
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] apiKeySecretBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(apiKeySecretBytes);
    }

}
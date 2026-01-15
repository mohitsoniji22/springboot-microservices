package com.example.os.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.*;
import io.jsonwebtoken.security.*;
import lombok.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.*;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    public Claims validateAndGetClaims(String token) {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);

            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(keyBytes))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException ex) {
            throw new RuntimeException("Invalid JWT token", ex);
        }
    }

    public CurrentUser extractCurrentUser(String token) {
        Claims claims = validateAndGetClaims(token);
        Long userId = claims.get("userId", Long.class);
        String email = claims.get("email", String.class);
        String username = claims.getSubject();
        return new CurrentUser(userId, username, email);
    }

    public CurrentUser getCurrentUser() {
        return (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}

package com.example.security_service.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.example.security_service.entity.*;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.security_service.repository.RefreshTokenRepository;
import com.example.security_service.repository.UserRepository;


@Slf4j
@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByUsername(username).get();

        Optional<RefreshToken> existingToken =
                refreshTokenRepository.findByUser(user);

        try{
            RefreshToken refreshToken = existingToken.orElse(RefreshToken.builder()
                    .user(user) // 100 minutes
                    .build());
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(java.time.Instant.now().plusMillis(6000000));
            log.info("Refresh token {} created for user {}", refreshToken.getToken(), username);
            return refreshTokenRepository.save(refreshToken);
        } catch (Exception e) {
            throw new RuntimeException("Token expired");
        }
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            log.info("Refresh token {} expired", token.getToken());
            throw new RuntimeException(token.getToken() + " Refresh token was expired. Please make a new signin request");
        }
        return token;
    }
}
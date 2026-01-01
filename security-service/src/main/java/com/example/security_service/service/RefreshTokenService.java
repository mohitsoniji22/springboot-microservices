package com.example.security_service.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.example.security_service.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.security_service.repository.RefreshTokenRepository;
import com.example.security_service.repository.UserRepository;


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
            System.out.println("Refresh token inside RefreshTokenService: " + refreshToken.getToken());
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
            throw new RuntimeException(token.getToken() + " Refresh token was expired. Please make a new signin request");
        }
        return token;
    }
}
package com.example.security_service.service;

import com.example.security_service.exception.*;
import lombok.extern.slf4j.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.security_service.dto.AuthRequest;
import com.example.security_service.dto.JwtResponse;
import com.example.security_service.dto.RefreshTokenRequest;
import com.example.security_service.entity.RefreshToken;
import com.example.security_service.entity.User;
import com.example.security_service.repository.UserRepository;

import java.util.*;

@Slf4j
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
    }

    public String saveUser(User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        User userNew = existingUser.orElse(user);
        userNew.setPassword(passwordEncoder.encode(user.getPassword()));
        userNew = userRepository.save(userNew);
        log.info("User {} registered successfully", user.getUsername());
        return "User registered successfully. Username: " + userNew.getUsername() + ", userId: " + userNew.getUserId();
    }

    public JwtResponse generateToken(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate
                (new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));

        if (authentication.isAuthenticated()) {
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequest.username());
            log.info("Refresh token has been created for user {}", authRequest.username());
            return JwtResponse.builder()
                    .token(refreshToken.getToken())
                    .accessToken(jwtService.generateToken(authRequest.username()))
                    .build();
        } else {
            throw new InvalidCredentialsException("Invalid user request!!");
        }
    }

    public void validateToken(String token) {
        jwtService.validateToken(token);
        log.info("Token has been validated successfully {}", token);
    }

    public JwtResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        return refreshTokenService.findByToken(refreshTokenRequest.token())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtService.generateToken(user.getUsername());
                    log.info("Refresh token has been created for user {}", user.getUsername());
                    return JwtResponse.builder()
                            .accessToken(accessToken)
                            .token(refreshTokenRequest.token())
                            .build();
                })
                .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token"));
    }
}
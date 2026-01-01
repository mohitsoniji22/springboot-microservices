package com.example.security_service.service;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public String saveUser(User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        User userNew = existingUser.orElse(user);

        userNew.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(userNew);
        return "User saved successfully";
    }

    public JwtResponse generateToken(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate
                (new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        if(authentication.isAuthenticated()) {
            System.out.println("User authenticated!!");
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequest.getUsername());
            System.out.println("Refresh token: " + refreshToken.getToken());
            return JwtResponse.builder()
                    .token(refreshToken.getToken())
                    .accessToken(jwtService.generateToken(authRequest.getUsername()))
                    .build();
        }
        else{
            throw new RuntimeException("Invalid user request!!");
        }
    }

    public void validateToken(String token) {
        jwtService.validateToken(token);
    }

    public JwtResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        return refreshTokenService.findByToken(refreshTokenRequest.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtService.generateToken(user.getUsername());
                    return JwtResponse.builder()
                            .accessToken(accessToken)
                            .token(refreshTokenRequest.getToken())
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

    }

}
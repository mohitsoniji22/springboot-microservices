package com.example.security_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.security_service.dto.AuthRequest;
import com.example.security_service.dto.JwtResponse;
import com.example.security_service.dto.RefreshTokenRequest;
import com.example.security_service.entity.User;
import com.example.security_service.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // @Autowired
    // private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        return authService.saveUser(user);
    }

    @GetMapping("/getToken")
    public JwtResponse getToken(@RequestBody AuthRequest authRequest) {
        return authService.generateToken(authRequest);
    }

    @GetMapping("/validateToken")
    public String validateToken(@RequestParam("token") String token) {
        authService.validateToken(token);
        return "Token is valid";
    }

    @GetMapping("/refreshToken")
    public JwtResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest);
    }
}
package com.example.security_service.dto;

import lombok.*;

@Builder
public record JwtResponse(String token, String accessToken) {
}
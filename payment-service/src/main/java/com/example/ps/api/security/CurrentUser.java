package com.example.ps.api.security;

public record CurrentUser(Long userId, String username, String email) {
}

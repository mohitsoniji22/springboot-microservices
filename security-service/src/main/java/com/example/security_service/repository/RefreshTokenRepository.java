package com.example.security_service.repository;

import java.util.Optional;

import com.example.security_service.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);
}
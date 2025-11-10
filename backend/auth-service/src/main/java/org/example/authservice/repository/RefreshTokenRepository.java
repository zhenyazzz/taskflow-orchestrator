package org.example.authservice.repository;

import org.example.authservice.model.RefreshToken;
import org.example.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    
    Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);
    
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user = :user AND rt.revoked = false AND rt.expiresAt > :now")
    java.util.List<RefreshToken> findActiveTokensByUser(@Param("user") User user, @Param("now") LocalDateTime now);
    
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true, rt.revokedAt = :now WHERE rt.user = :user AND rt.revoked = false")
    int revokeAllUserTokens(@Param("user") User user, @Param("now") LocalDateTime now);
    
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now OR rt.revoked = true")
    int deleteExpiredAndRevokedTokens(@Param("now") LocalDateTime now);
}


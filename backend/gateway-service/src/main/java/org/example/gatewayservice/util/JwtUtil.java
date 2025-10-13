package org.example.gatewayservice.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.example.events.enums.Role;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private final SecretKey key;
    private final JwtParser jwtParser;
    private final int jwtExpirationMs;
    private final String issuer;

    public JwtUtil(
            @Value("${JWT_SECRET_KEY:aXv7j3tR8kL9mQp2sV5y7x9A1zC4E7H0bW3cZ6u8oP1dF4rT5nJ9iK2lO0q}") String jwtSecret,
            @Value("${jwt.access-token-expiration:86400000}") int jwtExpirationMs,
            @Value("${jwt.issuer:auth-service}") String issuer) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        this.jwtParser = Jwts.parser().verifyWith(key).build();
        this.jwtExpirationMs = jwtExpirationMs;
        this.issuer = issuer;
    }

    public String getJwtFromHeader(String bearerToken) {
        logger.debug("Authorization Header: {}", bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    

    

    public String getUserNameFromJwtToken(String token) {
        return jwtParser
                .parseSignedClaims(token)
                .getPayload().getSubject();
    }

    public UUID getUserIdFromJwtToken(String token) {
        Claims claims = jwtParser
                .parseSignedClaims(token)
                .getPayload();

        String userIdStr = claims.get("userId", String.class);
        return UUID.fromString(userIdStr);
    }



    public boolean validateJwtToken(String authToken) {
        try {
            logger.debug("Validate");
            jwtParser.parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public List<Role> getRoles(String token) {
        Claims claims = jwtParser
                .parseSignedClaims(token)
                .getPayload();

        List<String> roles = claims.get("roles", List.class);
        return roles.stream()
                .map(Role::valueOf)
                .toList();
    }


}
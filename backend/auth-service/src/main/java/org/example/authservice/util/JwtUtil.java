package org.example.authservice.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import org.example.authservice.model.User;
import org.example.events.enums.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private final SecretKey key;
    private final JwtParser jwtParser;
    private final int accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;
    private final String issuer;

    public JwtUtil(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.access-token-expiration}") int accessTokenExpirationMs,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpirationMs,
            @Value("${spring.application.name}") String issuer) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        this.jwtParser = Jwts.parser().verifyWith(key).build();
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
        this.issuer = issuer;
    }

    public String getJwtFromHeader(String bearerToken) {
        logger.debug("Authorization Header: {}", bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        logger.debug("Authorization Header: {}", bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String getRefreshJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("refreshToken")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public String generateAccessToken(User user) {
        String username = user.getUsername();
        List<String> roles = user.getRoles()
                .stream()
                .map(Enum::name)
                .toList();
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .claim("userId", user.getId().toString())
                .claim("tokenType", "ACCESS")
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(key)
                .compact();
    }

    public String generateAccessTokenFromUsername(Authentication authentication, UUID userId) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .claim("userId", userId.toString())
                .claim("tokenType", "ACCESS")
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(UUID userId) {
        UUID tokenId = UUID.randomUUID();
        return Jwts.builder()
                .subject(userId.toString())
                .claim("tokenType", "REFRESH")
                .claim("jti", tokenId.toString())
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .signWith(key)
                .compact();
    }

    @Deprecated
    public String generateTokenFromUser(User user) {
        return generateAccessToken(user);
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
        if (userIdStr == null) {
            // Для refresh токена userId может быть в subject
            userIdStr = claims.getSubject();
        }
        return UUID.fromString(userIdStr);
    }

    public UUID getUserIdFromRefreshToken(String token) {
        Claims claims = jwtParser
                .parseSignedClaims(token)
                .getPayload();
        // В refresh токене userId хранится в subject
        return UUID.fromString(claims.getSubject());
    }



    public boolean validateJwtToken(String authToken) {
        try {
            logger.debug("Validate access token");
            Claims claims = jwtParser.parseSignedClaims(authToken).getPayload();
            String tokenType = claims.get("tokenType", String.class);
            if (tokenType == null || !"ACCESS".equals(tokenType)) {
                logger.error("Invalid token type: expected ACCESS, got {}", tokenType);
                return false;
            }
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

    public boolean validateRefreshToken(String token) {
        try {
            logger.debug("Validate refresh token");
            Claims claims = jwtParser.parseSignedClaims(token).getPayload();
            String tokenType = claims.get("tokenType", String.class);
            if (tokenType == null || !"REFRESH".equals(tokenType)) {
                logger.error("Invalid token type: expected REFRESH, got {}", tokenType);
                return false;
            }
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid refresh token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Refresh token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Refresh token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Refresh token claims string is empty: {}", e.getMessage());
        }
        return false;
    }
    
    @SuppressWarnings("unchecked")
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
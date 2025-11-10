package org.example.authservice.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    @Value("${jwt.cookie-name:refreshToken}")
    private String cookieName;

    @Value("${jwt.cookie-secure:true}")
    private boolean cookieSecure;

    @Value("${jwt.cookie-http-only:true}")
    private boolean cookieHttpOnly;

    @Value("${jwt.cookie-same-site:strict}")
    private String cookieSameSite;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpirationMs;

    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, refreshToken)
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .path("/")
                .maxAge(refreshTokenExpirationMs / 1000)
                .sameSite(cookieSameSite)
                .build();
        
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite(cookieSameSite)
                .build();
        
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}


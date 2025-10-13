package org.example.gatewayservice.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import org.example.gatewayservice.util.JwtUtil;
import org.example.events.enums.Role;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/", "/public/", "/eureka/", "/actuator/", "/favicon.ico"
    );

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // Пропускаем публичные эндпоинты
        if (isPublicEndpoint(path)) {
            return chain.filter(exchange);
        }

        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String token = jwtUtil.getJwtFromHeader(authorizationHeader);

        // Для защищенных эндпоинтов токен обязателен
        if (token == null) {
            return unauthorized(exchange, "Missing JWT token");
        }

        if (!jwtUtil.validateJwtToken(token)) {
            return unauthorized(exchange, "Invalid JWT token");
        }

        try {
            String username = jwtUtil.getUserNameFromJwtToken(token);
            List<Role> roles = jwtUtil.getRoles(token);

            if (username == null || username.isEmpty()) {
                return unauthorized(exchange, "Invalid username in JWT");
            }

            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                    .collect(Collectors.toList());

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username, token, authorities); // Сохраняем токен в credentials

            // Устанавливаем аутентификацию в контекст
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

        } catch (Exception e) {
            return unauthorized(exchange, "JWT processing error: " + e.getMessage());
        }
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        System.err.println("JWT Auth failed: " + message);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("X-Auth-Error", message);
        return exchange.getResponse().setComplete();
    }
}
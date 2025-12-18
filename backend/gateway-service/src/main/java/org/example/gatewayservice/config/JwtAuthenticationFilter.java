package org.example.gatewayservice.config;

import org.example.gatewayservice.util.Role;
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

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/signUp",
            "/api/auth/signIn",
            "/api/auth/refresh",
            "/api/auth/logout",
            "/public/",
            "/eureka/",
            "/actuator",
            "/actuator/",
            "/favicon.ico"
    );

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().name();

        String upgrade = request.getHeaders().getUpgrade();
        if (upgrade != null && upgrade.equalsIgnoreCase("websocket")) {
            return chain.filter(exchange);
        }

        if ("OPTIONS".equalsIgnoreCase(method)) {
            return chain.filter(exchange);
        }

        if (isPublicEndpoint(path)) {
            return chain.filter(exchange);
        }

        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String token = jwtUtil.getJwtFromHeader(authorizationHeader);

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
                    .map(role -> new SimpleGrantedAuthority(role.name()))
                    .toList();

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(username, token, authorities);

            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

        } catch (Exception e) {
            return unauthorized(exchange, "JWT processing error: " + e.getMessage());
        }
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_PATHS.stream()
                .anyMatch(publicPath -> path.equals(publicPath) || path.startsWith(publicPath))
                || path.startsWith("/actuator/");
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("X-Auth-Error", message);
        return exchange.getResponse().setComplete();
    }
}

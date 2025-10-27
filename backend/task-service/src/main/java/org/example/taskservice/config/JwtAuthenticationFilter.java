package org.example.taskservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.taskservice.model.UserDetailsImpl;
import org.example.taskservice.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Пропускаем Swagger и публичные endpoints без проверки токена
        if (isPublicEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = jwtUtil.getJwtFromHeader(request);
        final String username = jwtUtil.getUserNameFromJwtToken(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UUID id = jwtUtil.getUserIdFromJwtToken(jwt);
            Collection<? extends GrantedAuthority> authorities = jwtUtil.getRoles(jwt)
                    .stream().map(role -> new SimpleGrantedAuthority(role.name())).collect(Collectors.toSet());
            UserDetailsImpl userDetails = new UserDetailsImpl(id, username, authorities);

            if (jwtUtil.validateJwtToken(jwt)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
    private boolean isPublicEndpoint(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        // Swagger endpoints
        if (path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources") ||
                path.equals("/swagger-ui.html") ||
                path.equals("/openapi")) {
            return true;
        }

        // Actuator health endpoint
        if (path.equals("/actuator/health")) {
            return true;
        }

        // Public API endpoints (если есть)
        if ("GET".equals(method) && path.startsWith("/api/users")) {
            return true;
        }

        return false;
    }
}


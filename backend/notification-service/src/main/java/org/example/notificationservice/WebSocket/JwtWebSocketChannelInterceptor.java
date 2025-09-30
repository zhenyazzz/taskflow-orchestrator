package org.example.notificationservice.WebSocket;

import org.example.notificationservice.util.JwtUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtWebSocketChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            handleConnect(accessor);
        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            handleSubscribe(accessor);
        }

        return message;
    }


    private void handleSubscribe(StompHeaderAccessor accessor) {
        Authentication auth = (Authentication) accessor.getUser();
        String destination = accessor.getDestination();

        if (destination != null && destination.startsWith("/topic/notifications.")) {
            if (auth == null) {
                throw new AccessDeniedException("Authentication required for subscription");
            }

            // Извлекаем userId из destination
            String targetUserId = destination.substring("/topic/notifications.".length());
            
            // Получаем userId из аутентификации
            WebSocketUserDetails userDetails = (WebSocketUserDetails) auth.getDetails();
            UUID authenticatedUserId = userDetails.userId();

            // Проверяем, что пользователь подписывается на свои уведомления
            if (!authenticatedUserId.toString().equals(targetUserId)) {
                log.warn("User {} attempted to subscribe to notifications for user {}", 
                        authenticatedUserId, targetUserId);
                throw new AccessDeniedException("Access denied to notifications");
            }

            log.info("User {} subscribed to notifications", authenticatedUserId);
        }
    }


    private void handleConnect(StompHeaderAccessor accessor) {
        List<String> authorization = accessor.getNativeHeader("Authorization");
        String authToken = (authorization != null && !authorization.isEmpty()) ? authorization.get(0) : null;

        if (authToken != null && authToken.startsWith("Bearer ")) {
            String jwt = authToken.substring(7);

            if (jwtUtil.validateJwtToken(jwt)) {
                String username = jwtUtil.getUserNameFromJwtToken(jwt);
                UUID userId = jwtUtil.getUserIdFromJwtToken(jwt);
                List<SimpleGrantedAuthority> authorities = jwtUtil.getRoles(jwt).stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                        .collect(Collectors.toList());

                UserDetails userDetails = new User(username, "", authorities);
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                
                // Сохраняем userId в аутентификации для дальнейшей проверки
                authentication.setDetails(new WebSocketUserDetails(userId, username));
                
                accessor.setUser(authentication);
                log.info("User {} connected via WebSocket. User ID: {}", username, userId);
            } else {
                log.warn("Invalid JWT token for WebSocket connection.");
                throw new IllegalArgumentException("Invalid JWT token");
            }
        } else {
            log.warn("Missing or invalid Authorization header for WebSocket connection.");
            throw new IllegalArgumentException("Missing or invalid Authorization header");
        }
    }

    
}

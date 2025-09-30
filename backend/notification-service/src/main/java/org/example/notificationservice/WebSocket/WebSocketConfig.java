package org.example.notificationservice.WebSocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.config.ChannelRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtWebSocketChannelInterceptor jwtWebSocketChannelInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
        .setAllowedOriginPatterns("http://localhost:3000", "http://your-frontend-domain.com") // Укажи конкретные домены фронтенда
        .withSockJS();

        registry.addEndpoint("/ws/notifications")
                .setAllowedOriginPatterns("http://localhost:3000", "http://your-frontend-domain.com") // Укажи конкретные домены фронтенда
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtWebSocketChannelInterceptor);
    }
}

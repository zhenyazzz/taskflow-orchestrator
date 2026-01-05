package org.example.notificationservice.WebSocket;

import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.messaging.simp.SimpMessageType;

import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSocketMessageSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
            .simpTypeMatchers(SimpMessageType.CONNECT).permitAll()
            
            .simpSubscribeDestMatchers("/topic/notifications.*").authenticated()
            .simpSubscribeDestMatchers("/user/queue/notifications").authenticated()
            
            .simpDestMatchers("/app/**").denyAll()
            .anyMessage().denyAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true; 
    }

}

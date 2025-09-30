package org.example.notificationservice.WebSocket;

import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.messaging.simp.SimpMessageType;

public class WebSocketMessageSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
            // Разрешаем CONNECT всем (для установки соединения)
            .simpTypeMatchers(SimpMessageType.CONNECT).permitAll()
            
            // Защищаем подписку на уведомления - пользователь может подписаться только на свои уведомления
            .simpSubscribeDestMatchers("/topic/notifications.*").authenticated()
            .simpSubscribeDestMatchers("/user/queue/notifications").authenticated()
            
            // Блокируем отправку сообщений через WebSocket (только получение уведомлений)
            .simpDestMatchers("/app/**").denyAll()
            .anyMessage().denyAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true; // Отключаем проверку same origin для WebSocket
    }

}

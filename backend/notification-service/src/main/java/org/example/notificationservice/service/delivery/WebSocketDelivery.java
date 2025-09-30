package org.example.notificationservice.service.delivery;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
@Component
public class WebSocketDelivery {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketDelivery(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public Mono<Void> sendWebSocketNotification(String userId, String type, Object event) {
        return Mono.fromRunnable(() -> {
            String destination = "/topic/notifications." + userId;
            messagingTemplate.convertAndSend(destination, event);
            log.info("Sending WebSocket notification to user {} of type {} with event {}", userId, type, event);
        });
    }
}

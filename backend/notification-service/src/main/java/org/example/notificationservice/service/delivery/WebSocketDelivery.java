package org.example.notificationservice.service.delivery;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import lombok.RequiredArgsConstructor;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketDelivery {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendWebSocketNotification(String userId, String type, Object notification) {
        messagingTemplate.convertAndSendToUser(
            userId,
            "/queue/notifications",
            notification
        );
    
        log.info("WS -> user={}, type={}, payload={}", userId, type, notification);
    }
    
}

package org.example.userservice.kafka.producer;

import java.util.UUID;

import org.example.events.user.UserCreatedEvent;
import org.example.events.user.UserDeletedEvent;
import org.example.events.user.UserProfileUpdatedEvent;
import org.example.userservice.kafka.KafkaTopicsProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
    
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicsProperties topics;

    public <T> void sendEvent(String topic,String key, T event) {
        log.info("Sending event to topic {}: {}", topic, event);
        kafkaTemplate.send(topic,key, event)
        .whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Ошибка при отправке события {} в топик {}: {}", event, topic, ex.getMessage(), ex);
            } else {
                log.info("Событие {} отправлено в топик {} partition={} offset={}",
                        event, topic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }

    public void sendUserCreatedEvent(UUID key, UserCreatedEvent event) {
        log.info("Отправляем UserCreatedEvent с ключом {}: {}", key, event);
        sendEvent(topics.getUserCreated(), key.toString(), event);
    }

    public void sendUserDeletedEvent(UUID key, UserDeletedEvent event) {
        log.info("Отправляем UserDeletedEvent с ключом {}: {}", key, event);
        sendEvent(topics.getUserDelete(), key.toString(), event);
    }

    public void sendUserProfileUpdatedEvent(UUID key, UserProfileUpdatedEvent event) {
        log.info("Отправляем UserRoleUpdateEvent с ключом {}: {}", key, event);
        sendEvent(topics.getUserProfileUpdate(), key.toString(), event);
    }

}
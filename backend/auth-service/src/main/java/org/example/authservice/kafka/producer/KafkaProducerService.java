package org.example.authservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import org.example.authservice.kafka.KafkaTopicsProperties;
import org.example.events.user.LoginFailEvent;
import org.example.events.user.UserLoginEvent;
import org.example.events.user.UserRegistrationEvent;
import org.example.events.user.UserRoleUpdateEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicsProperties kafkaTopicsProperties;
    
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

    public void sendUserRegistrationEvent(UUID key, UserRegistrationEvent event) {
        log.info("Отправляем UserRegistrationEvent с ключом {}: {}", key, event);
        sendEvent(kafkaTopicsProperties.getUserRegistered(), key.toString(), event);
    }

    public void sendUserLoginEvent(UUID key, UserLoginEvent event) {
        log.info("Отправляем UserLoginEvent с ключом {}: {}", key, event);
        sendEvent(kafkaTopicsProperties.getUserLoginSuccess(), key.toString(), event);
    }

    public void sendUserRoleUpdateEvent(UUID key, UserRoleUpdateEvent event) {
        log.info("Отправляем UserRoleUpdateEvent с ключом {}: {}", key, event);
        sendEvent(kafkaTopicsProperties.getUserRoleUpdated(), key.toString(), event);
    }

    public void sendLoginFailEvent(String key, LoginFailEvent event) {
        log.info("Отправляем LoginFailEvent с ключом {}: {}", key, event);
        sendEvent(kafkaTopicsProperties.getUserLoginFailed(), key, event);
    }
}

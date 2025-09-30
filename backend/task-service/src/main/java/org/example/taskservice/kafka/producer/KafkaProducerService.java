package org.example.taskservice.kafka.producer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.events.attachment.AttachmentAddedEvent;
import org.example.events.attachment.AttachmentDeletedEvent;
import org.example.events.task.TaskAssigneesUpdatedEvent;
import org.example.events.task.TaskCompletedEvent;
import org.example.events.task.TaskCreatedEvent;
import org.example.events.task.TaskDeletedEvent;
import org.example.events.task.TaskStatusUpdatedEvent;
import org.example.events.task.TaskSubscribedEvent;
import org.example.events.task.TaskUnsubscribedEvent;
import org.example.events.task.TaskUpdatedEvent;
import org.example.taskservice.config.KafkaTopicsProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicsProperties topics;

    public <T> void sendEvent(String topic, String key, T event) {
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

    public void sendTaskCreatedEvent(String key, TaskCreatedEvent event) {
        log.info("Sending TaskCreatedEvent with key {}: {}", key, event);
        sendEvent(topics.getTaskCreated(), key, event);
    }

    public void sendTaskUpdatedEvent(String id, TaskUpdatedEvent taskUpdatedEvent) {
        log.info("Sending TaskUpdatedEvent with key {}: {}", id, taskUpdatedEvent);
        sendEvent(topics.getTaskUpdated(), id, taskUpdatedEvent);
    }

    public void sendTaskSubscribedEvent(String id, TaskSubscribedEvent taskSubscribedEvent) {
        log.info("Sending TaskSubscribedEvent with key {}: {}", id, taskSubscribedEvent);
        sendEvent(topics.getTaskSubscribed(), id, taskSubscribedEvent);
    }

    public void sendTaskUnsubscribedEvent(String id, TaskUnsubscribedEvent taskUnsubscribedEvent) {
        log.info("Sending TaskUnsubscribedEvent with key {}: {}", id, taskUnsubscribedEvent);
        sendEvent(topics.getTaskUnsubscribed(), id, taskUnsubscribedEvent);
    }

    public void sendTaskCompletedEvent(String id, TaskCompletedEvent taskCompletedEvent) {
        log.info("Sending TaskCompletedEvent with key {}: {}", id, taskCompletedEvent);
        sendEvent(topics.getTaskCompleted(), id, taskCompletedEvent);
    }

    public void sendTaskDeletedEvent(String id, TaskDeletedEvent taskDeletedEvent) {
        log.info("Sending TaskDeletedEvent with key {}: {}", id, taskDeletedEvent);
        sendEvent(topics.getTaskDeleted(), id, taskDeletedEvent);
    }

    public void sendTaskStatusUpdatedEvent(String id, TaskStatusUpdatedEvent taskStatusUpdatedEvent) {
        log.info("Sending TaskStatusUpdatedEvent with key {}: {}", id, taskStatusUpdatedEvent);
        sendEvent(topics.getTaskStatusUpdated(), id, taskStatusUpdatedEvent);
    }

    public void sendTaskAssigneesUpdatedEvent(String id, TaskAssigneesUpdatedEvent taskAssigneesUpdatedEvent) {
        log.info("Sending TaskAssigneesUpdatedEvent with key {}: {}", id, taskAssigneesUpdatedEvent);
        sendEvent(topics.getTaskAssigneesUpdated(), id, taskAssigneesUpdatedEvent);
    }

    public void sendAttachmentAddedEvent(String id, AttachmentAddedEvent attachmentAddedEvent) {
        log.info("Sending AttachmentAddedEvent with key {}: {}", id, attachmentAddedEvent);
        sendEvent(topics.getAttachmentCreated(), id, attachmentAddedEvent);
    }

    public void sendAttachmentDeletedEvent(String attachmentId, AttachmentDeletedEvent attachmentDeletedEvent) {
        log.info("Sending AttachmentDeletedEvent with key {}: {}", attachmentId, attachmentDeletedEvent);
        sendEvent(topics.getAttachmentDeleted(), attachmentId, attachmentDeletedEvent);
    }



}

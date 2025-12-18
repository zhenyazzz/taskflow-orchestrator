package org.example.notificationservice.dto.response;

import org.example.notificationservice.model.NotificationType;

import java.time.LocalDateTime;
import java.util.Map;

public record NotificationDto(
    String id,
    String message,
    NotificationType type,
    Map<String, String> metadata,
    LocalDateTime createdAt
) {}

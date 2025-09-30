package org.example.notificationservice.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;
    private String userId;
    private String message;
    private String type;
    private Map<String, String> metadata;
    private boolean read;
    @CreatedDate
    private LocalDateTime createdAt;
}

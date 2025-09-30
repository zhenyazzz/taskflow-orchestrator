package org.example.analyticsservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.example.events.enums.TaskPriority;
import org.example.events.enums.TaskStatus;

import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "task_events", indexes = {
    @Index(name = "idx_task_events_task_id", columnList = "task_id"),
    @Index(name = "idx_task_events_event_type", columnList = "event_type"),
    @Index(name = "idx_task_events_timestamp", columnList = "event_timestamp")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id", nullable = false)
    private String taskId;

    @Column(name = "event_type", nullable = false)
    private TaskEventType eventType; 

    @Column(name = "event_data", columnDefinition = "JSONB")
    private String eventData; 

    @Column(name = "title")
    private String title; 

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private TaskPriority priority; 

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus status; 

    @Column(name = "assignee_ids")
    private Set<String> assigneeIds; 

    @Column(name = "creator_id")
    private String creatorId; 

    @Column(name = "completion_time")
    private Instant completionTime;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "event_timestamp", nullable = false)
    private Instant eventTimestamp;

    @PrePersist
    protected void onCreate() {
        eventTimestamp = Instant.now();
    }
}

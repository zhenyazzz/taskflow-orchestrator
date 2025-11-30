package org.example.analyticsservice.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

/**
 * Глобальный счетчик задач
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "task_counts")
public class TaskCounter {

    @Id
    private String id;

    @Field("total_tasks")
    @Builder.Default
    private Long totalTasks = 0L;

    @Field("last_updated")
    @Builder.Default
    private Instant lastUpdated = Instant.now();
}


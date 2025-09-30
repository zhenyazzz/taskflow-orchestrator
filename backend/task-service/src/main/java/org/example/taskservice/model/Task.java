package org.example.taskservice.model;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.events.enums.Department;
import org.example.events.enums.TaskPriority;
import org.example.events.enums.TaskStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Document(collection = "tasks")
@CompoundIndexes({
        @CompoundIndex(name = "status_department_idx", def = "{'status': 1, 'department': 1}", partialFilter = "{status : 'AVAILABLE'}"),
        @CompoundIndex(name = "assignee_status_idx", def = "{'assigneeIds': 1, 'status': 1}")
        }
)
@Data
public class Task {

    @Id
    private String id;

    @NotNull
    private String title;

    private String description;

    @NotNull
    @Indexed(unique=false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @Indexed
    private Set<String> assigneeIds;

    @Indexed
    private String creatorId;

    @Indexed
    @Enumerated(EnumType.STRING)
    private Department department;

    @NotNull
    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Indexed(direction = IndexDirection.DESCENDING)
    private Instant dueDate;

    private List<String> tags;

    private List<Comment> comments = new ArrayList<>();
}

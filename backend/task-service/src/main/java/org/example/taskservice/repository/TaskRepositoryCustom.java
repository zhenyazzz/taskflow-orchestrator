package org.example.taskservice.repository;

import org.example.events.enums.Department;
import org.example.events.enums.TaskStatus;
import org.example.taskservice.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskRepositoryCustom {
    Page<Task> findTasksByFilters(TaskStatus status, String assigneeId, String creatorId, Department department, Pageable pageable);
}



package org.example.taskservice.repository;

import org.example.events.enums.Department;
import org.example.events.enums.TaskStatus;
import org.example.events.enums.TaskPriority;
import org.example.taskservice.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskRepositoryCustom {
    Page<Task> findTasksByFilters(TaskStatus status,
                                  TaskPriority priority,
                                  String assigneeId,
                                  String creatorId,
                                  Department department,
                                  String search,
                                  Pageable pageable);
    Page<Task> findTasksByAssigneeWithFilters(String assigneeId, TaskStatus status, String creatorId, Department department, Pageable pageable);
}



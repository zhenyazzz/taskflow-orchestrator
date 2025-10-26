package org.example.taskservice.repository;

import org.example.events.enums.TaskStatus;
import org.example.events.enums.Department;
import org.example.taskservice.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<Task, String>, TaskRepositoryCustom {

    Page<Task> findByStatusAndDepartment(TaskStatus status, Department department, Pageable pageable);
    Page<Task> findByAssigneeIdsContains(String userId, Pageable pageable);
    Page<Task> findByAssigneeIdsContainsAndStatus(String userId, TaskStatus status, Pageable pageable);
    Page<Task> findByAssigneeIdsContainsAndStatusIn(String userId, List<TaskStatus> statuses, Pageable pageable);

    // Due soon queries
    Page<Task> findByDueDateBetween(Instant from, Instant to, Pageable pageable);
    Page<Task> findByDueDateBetweenAndStatus(Instant from, Instant to, TaskStatus status, Pageable pageable);
    Page<Task> findByDueDateBetweenAndAssigneeIds(Instant from, Instant to, String assigneeId, Pageable pageable);
    Page<Task> findByDueDateBetweenAndStatusAndAssigneeIds(Instant from, Instant to, TaskStatus status, String assigneeId, Pageable pageable);

    // Custom filter search is implemented in TaskRepositoryImpl via MongoTemplate
}

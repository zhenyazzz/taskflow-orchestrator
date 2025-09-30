package org.example.taskservice.repository;

import org.example.events.enums.TaskStatus;
import org.example.taskservice.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {

    Page<Task> findByStatusAndDepartment(TaskStatus available, String department, PageRequest of);
    Page<Task> findByAssigneeIdsContaining(String userId, PageRequest of);
    Page<Task> findByAssigneeIdsContainingAndStatus(String userId, String status, PageRequest of);

    @Query("SELECT t FROM Task t WHERE " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:assigneeId IS NULL OR :assigneeId MEMBER OF t.assigneeIds) AND " +
            "(:creatorId IS NULL OR t.creatorId = :creatorId) AND " +
            "(:department IS NULL OR t.department = :department)")
    Page<Task> findTasksByFilters(
            @Param("status") String status,
            @Param("assigneeId") String assigneeId,
            @Param("creatorId") String creatorId,
            @Param("department") String department,
            Pageable pageable);
}

package org.example.analyticsservice.repository.mongodb;

import org.example.analyticsservice.model.mongo.TaskDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskDocumentRepository extends MongoRepository<TaskDocument, String> {

    Optional<TaskDocument> findByTaskId(String taskId);

    List<TaskDocument> findByCreatorId(String creatorId);

    List<TaskDocument> findByAssigneeIdsContaining(String assigneeId);

    @Query("{ 'assignee_ids': ?0, 'is_deleted': false }")
    List<TaskDocument> findActiveTasksByAssignee(String assigneeId);

    @Query("{ 'creator_id': ?0, 'is_deleted': false }")
    List<TaskDocument> findActiveTasksByCreator(String creatorId);

    @Query("{ 'is_completed': true, 'is_deleted': false }")
    List<TaskDocument> findAllCompletedTasks();

    @Query("{ 'is_deleted': false }")
    List<TaskDocument> findAllActiveTasks();

    long countByIsCompletedAndIsDeleted(Boolean isCompleted, Boolean isDeleted);

    long countByIsDeleted(Boolean isDeleted);
}



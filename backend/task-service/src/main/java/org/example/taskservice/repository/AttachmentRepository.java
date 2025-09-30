package org.example.taskservice.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import org.example.taskservice.model.Attachment;

@Repository
public interface AttachmentRepository extends MongoRepository<Attachment, String> {
    List<Attachment> findByTaskId(String taskId);
}

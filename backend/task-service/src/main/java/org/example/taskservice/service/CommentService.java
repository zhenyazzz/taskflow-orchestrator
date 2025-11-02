package org.example.taskservice.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskservice.dto.request.comment.CreateCommentRequest;
import org.example.taskservice.dto.request.comment.UpdateCommentRequest;
import org.example.taskservice.dto.response.comment.CommentResponse;
import org.example.taskservice.exception.AccessDeniedException;
import org.example.taskservice.exception.CommentNotFoundException;
import org.example.taskservice.exception.TaskNotFoundException;
import org.example.taskservice.mapper.CommentMapper;
import org.example.taskservice.model.Comment;
import org.example.taskservice.model.Task;
import org.example.taskservice.model.UserDetailsImpl;
import org.example.taskservice.repository.TaskRepository;
import org.example.taskservice.kafka.producer.KafkaProducerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final TaskRepository taskRepository;
    private final CommentMapper commentMapper;
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public CommentResponse addComment(String taskId, @Valid CreateCommentRequest request) {
        log.info("Adding comment to task: {}", taskId);
        UserDetailsImpl details = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Task task = getTask(taskId);
        Comment comment = commentMapper.toComment(request, details.getId());
        task.getComments().add(comment);
        taskRepository.save(task);

        log.info("Comment added. ID: {}, Task: {}", comment.getId(), taskId);
        kafkaProducerService.sendCommentCreatedEvent(comment.getId(), commentMapper.toCommentCreatedEvent(comment, task.getId()));
        return commentMapper.toCommentResponse(comment);
    }

    public Page<CommentResponse> getComments(String taskId, PageRequest pageRequest) {
        log.debug("Getting comments for task: {}", taskId);
        Task task = getTask(taskId);

        List<CommentResponse> comments = task.getComments().stream()
                .skip(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .map(commentMapper::toCommentResponse)
                .toList();

        return new PageImpl<>(comments, pageRequest, task.getComments().size());
    }

    @Transactional
    public void deleteComment(String taskId, String commentId) {
        log.info("Deleting comment: {} from task: {}", commentId, taskId);
        UserDetailsImpl details = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUserId = details.getId();
        
        Task task = getTask(taskId);
        Comment comment = task.getComments().stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new CommentNotFoundException("Comment not found: " + commentId));
        
        boolean isAuthor = comment.getAuthorId().equals(currentUserId);
        boolean isAdmin = details.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
        
        if (!isAuthor && !isAdmin) {
            throw new AccessDeniedException("You don't have permission to delete this comment");
        }
        
        task.getComments().remove(comment);
        taskRepository.save(task);
        log.info("Comment {} deleted by user {}", commentId, currentUserId);
        kafkaProducerService.sendCommentDeletedEvent(commentId, commentMapper.toCommentDeletedEvent(comment, task.getId()));
    }

    @Transactional
    public CommentResponse updateComment(String taskId, String commentId, @Valid UpdateCommentRequest request) {
        log.info("Updating comment: {}", commentId);
        UserDetailsImpl details = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUserId = details.getId();
        
        Task task = getTask(taskId);

        Comment comment = task.getComments().stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new CommentNotFoundException("Comment not found: " + commentId));

        if (!comment.getAuthorId().equals(currentUserId)) {
            throw new AccessDeniedException("You don't have permission to update this comment");
        }

        commentMapper.updateComment(request, comment);
        taskRepository.save(task);

        log.info("Comment {} updated by user {}", commentId, currentUserId);
        kafkaProducerService.sendCommentUpdatedEvent(commentId, commentMapper.toCommentUpdatedEvent(comment, task.getId()));
        return commentMapper.toCommentResponse(comment);
    }

    private Task getTask(String taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.error("Task not found: {}", taskId);
                    return new TaskNotFoundException("Task not found: " + taskId);
                });
    }

}


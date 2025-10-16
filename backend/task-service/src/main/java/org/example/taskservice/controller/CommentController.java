package org.example.taskservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskservice.dto.request.comment.CreateCommentRequest;
import org.example.taskservice.dto.request.comment.UpdateCommentRequest;
import org.example.taskservice.dto.response.comment.CommentResponse;
import org.example.taskservice.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/task/{taskId}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable String taskId,
            @Valid @RequestBody CreateCommentRequest request) {
        log.info("addComment taskId={}, request={}", taskId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(taskId, request));
    }

    @GetMapping("/task/{taskId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<CommentResponse>> getComments(
            @PathVariable String taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("getComments taskId={}, page={}, size={}", taskId, page, size);
        return ResponseEntity.ok(commentService.getComments(taskId, PageRequest.of(page, size)));
    }

    @PutMapping("/task/{taskId}/{commentId}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable String taskId,
            @PathVariable String commentId,
            @Valid @RequestBody UpdateCommentRequest request) {
        log.info("addComment taskId={}, request={}", taskId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.updateComment(taskId,commentId, request));
    }

    @DeleteMapping("/task/{taskId}/{commentId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable String taskId, @PathVariable String commentId) {
        log.info("deleteComment taskId={}, commentId={}", taskId, commentId);
        commentService.deleteComment(taskId, commentId);
    }
}

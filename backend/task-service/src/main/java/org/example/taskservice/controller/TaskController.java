package org.example.taskservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskservice.dto.request.task.CreateTaskRequest;
import org.example.taskservice.dto.request.task.UpdateAssigneesRequest;
import org.example.taskservice.dto.request.task.UpdateStatusRequest;
import org.example.taskservice.dto.request.task.UpdateTaskRequest;
import org.example.taskservice.dto.response.task.TaskResponse;
import org.example.taskservice.model.UserDetailsImpl;
import org.example.taskservice.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tasks", description = "Task management operations")
public class TaskController {
    private final TaskService taskService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String assigneeId,
            @RequestParam(required = false) String creatorId,
            @RequestParam(required = false) String department) {
        log.info("Retrieving tasks: page={}, size={}, status={}, assigneeId={}, creatorId={}, department={}",
                page, size, status, assigneeId, creatorId, department);
        return ResponseEntity.ok(taskService.getTasks(PageRequest.of(page, size), status, assigneeId, creatorId, department));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        log.info("Retrieving all tasks for");
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable String id) {
        log.info("Retrieving task by id={}", id);
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestPart("task") CreateTaskRequest createTaskRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("Creating task: title={}, department={}, user={}, filesCount={}",
                createTaskRequest.title(), createTaskRequest.department(), userDetails.getUsername(),
                files != null ? files.size() : 0);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(createTaskRequest, userDetails.getId(), files));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable String id,
            @Valid @RequestBody UpdateTaskRequest updateTaskRequest) {
        log.info("Updating task id={}, title={}, user={}", id, updateTaskRequest.title());
        return ResponseEntity.ok(taskService.updateTask(id, updateTaskRequest));
    }


    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateStatusRequest request) {
        log.info("Updating task status id={}, status={}", id, request.status());
        return ResponseEntity.ok(taskService.updateStatus(id, request));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable String id) {
        log.info("Deleting task id={}", id);
        taskService.deleteTask(id);
    }


    @GetMapping("/assignee/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<TaskResponse>> getTasksByAssignee(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(required = false) String status) {
        log.info("Retrieving tasks for assignee userId={}, page={}, size={}, status={}",
                userId, page, size, status);
        return ResponseEntity.ok(taskService.getTasksByAssignee(userId, PageRequest.of(page, size), status));
    }

    @PatchMapping("/{id}/assignees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskResponse> updateAssignees(
            @PathVariable String id,
            @Valid @RequestBody UpdateAssigneesRequest request) {
        log.info("Updating assignees for task id={}, request={}", id, request);
        return ResponseEntity.ok(taskService.updateAssignees(id, request));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<TaskResponse>> createBulkTasks(
            @Valid @RequestBody List<CreateTaskRequest> requests,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("Creating {} tasks in bulk, user={}", requests.size(), userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createBulkTasks(requests, userDetails.getId()));
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<TaskResponse>> getTaskHistory(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Retrieving history for task id={}, page={}, size={}", id, page, size);
        return ResponseEntity.ok(taskService.getTaskHistory(id, PageRequest.of(page, size)));
    }

    @GetMapping("/due-soon")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<TaskResponse>> getDueSoonTasks(
            @RequestParam(defaultValue = "24") @Min(1) long hours,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String assigneeId) {
        log.info("Retrieving tasks due soon: hours={}, page={}, size={}, status={}, assigneeId={}",
                hours, page, size, status, assigneeId);
        return ResponseEntity.ok(taskService.getDueSoonTasks(hours, PageRequest.of(page, size), status, assigneeId));
    }
}

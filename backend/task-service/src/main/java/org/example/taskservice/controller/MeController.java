package org.example.taskservice.controller;


import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskservice.dto.response.task.TaskResponse;
import org.example.taskservice.model.UserDetailsImpl;
import org.example.taskservice.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/me/tasks")
@RequiredArgsConstructor
@Slf4j
public class MeController {
    private final TaskService taskService;

    @PatchMapping("/{id}/subscribe")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TaskResponse> subscribeToTask(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @PathVariable String id) {
        log.info("subscribeToTask id={}, userId={}, username={}", id, principal.getId(), principal.getUsername());
        return ResponseEntity.ok(taskService.subscribeToTask(id, principal.getId()));
    }

    @PatchMapping("/{id}/unsubscribe")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TaskResponse> unsubscribeFromTask(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @PathVariable String id) {
        log.info("unsubscribeFromTask id={}, userId={}, username={}", id, principal.getId(), principal.getUsername());
        return ResponseEntity.ok(taskService.unsubscribeFromTask(id, principal.getId()));
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TaskResponse> completeTask(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @PathVariable String id) {
        log.info("completeTask id={}, userId={}, username={}", id, principal.getId(), principal.getUsername());
        return ResponseEntity.ok(taskService.completeTask(id, principal.getId()));
    }

    @GetMapping("")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<TaskResponse>> getMyTasks(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String department,
            @AuthenticationPrincipal UserDetailsImpl principal) {
        log.info("getMyTasks userId={}, username={}, page={}, size={}, status={}, department={}",
                principal.getId(), principal.getUsername(), page, size, status, department);
        return ResponseEntity.ok(taskService.getMyTasks(principal.getId(),
                PageRequest.of(page, size),
                status,
                department));
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<TaskResponse>> getMyTaskHistory(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @AuthenticationPrincipal UserDetailsImpl principal
    ) {
        log.info("Retrieving my task history for userId={}, page={}, size={}, user={}", principal.getId(), page, size, principal.getUsername());
        return ResponseEntity.ok(taskService.getTaskHistory(principal.getId(), PageRequest.of(page, size)));
    }

    @GetMapping("/available-tasks")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<TaskResponse>> getAvailableTasks(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(required = false) String department) {
        log.info("getAvailableTasks page={}, size={}, department={}",
                 page, size, department);
        return ResponseEntity.ok(taskService.getAvailableTasks(PageRequest.of(page, size),
                department));
    }

}

package org.example.taskservice.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.events.enums.TaskStatus;
import org.example.events.enums.Department;
import org.example.taskservice.dto.request.task.CreateTaskRequest;
import org.example.taskservice.dto.request.task.UpdateAssigneesRequest;
import org.example.taskservice.dto.request.task.UpdateStatusRequest;
import org.example.taskservice.dto.request.task.UpdateTaskRequest;
import org.example.taskservice.dto.response.task.TaskResponse;
import org.example.taskservice.exception.TaskNotFoundException;
import org.example.taskservice.kafka.producer.KafkaProducerService;
import org.example.taskservice.model.Task;
import org.example.taskservice.model.UserDetailsImpl;
import org.example.taskservice.mapper.CommentMapper;
import org.example.taskservice.mapper.TaskMapper;
import org.example.taskservice.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.time.Duration;
import java.time.Instant;
import org.springframework.web.multipart.MultipartFile;
import org.example.taskservice.service.AttachmentService;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final CommentMapper commentMapper;
    private final KafkaProducerService kafkaProducerService;
    private final AttachmentService attachmentService;

    public List<TaskResponse> getAllTasks() {
        log.info("getting all tasks");
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(task -> taskMapper.toResponse(task, commentMapper))
                .toList();
    }

    public TaskResponse getTaskById(String id) {
        log.info("getting task by id: {}", id);
        Task task = findTaskById(id);
        return taskMapper.toResponse(task, commentMapper);
    }

    private Task findTaskById(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }

    @Transactional
    public TaskResponse createTask(@Valid CreateTaskRequest createTaskRequest, String id, List<MultipartFile> files) {
        log.info("creating task: {}", createTaskRequest);
        Task task = taskMapper.toTask(createTaskRequest, id);
        Task savedTask = taskRepository.save(task);
        log.debug("Task created with ID: {}", savedTask.getId());
        
        if (files != null && !files.isEmpty()) {
            log.info("Adding {} files to task {}", files.size(), savedTask.getId());
            attachmentService.addAttachments(savedTask.getId(), files);
        }
        
        kafkaProducerService.sendTaskCreatedEvent(savedTask.getId(), taskMapper.toTaskCreatedEvent(savedTask));
        
        Set<String> assigneeIds = savedTask.getAssigneeIds();
        if (assigneeIds != null && !assigneeIds.isEmpty()) {
            log.info("Sending TaskSubscribedEvent for {} assignees", assigneeIds.size());
            for (String assigneeId : assigneeIds) {
                kafkaProducerService.sendTaskSubscribedEvent(
                    savedTask.getId(), 
                    taskMapper.toTaskSubscribedEvent(savedTask, assigneeId)
                );
            }
        }
        
        return taskMapper.toResponse(savedTask, commentMapper);
    }

    @Transactional
    public TaskResponse updateTask(String id, @Valid UpdateTaskRequest updateTaskRequest) {
        log.info("updating task: {}", updateTaskRequest);
        Task task = findTaskById(id);
        taskMapper.updateTask(updateTaskRequest, task);
        Task updatedTask = taskRepository.save(task);
        log.debug("Task updated with ID: {}", updatedTask.getId());
        kafkaProducerService.sendTaskUpdatedEvent(updatedTask.getId(), taskMapper.toTaskUpdatedEvent(updatedTask));
        return taskMapper.toResponse(updatedTask, commentMapper);
    }

    @Transactional
    public TaskResponse subscribeToTask(String id, String userId) {
        log.info("subscribing to task: {}", id);
        Task task = findTaskById(id);
        if (task.getAssigneeIds() == null) {
            task.setAssigneeIds(new java.util.HashSet<>());
        }
        task.getAssigneeIds().add(userId);
        Task updatedTask = taskRepository.save(task);
        log.debug("Task subscribed with ID: {}", updatedTask.getId());
        kafkaProducerService.sendTaskSubscribedEvent(updatedTask.getId(), taskMapper.toTaskSubscribedEvent(updatedTask,userId));
        return taskMapper.toResponse(updatedTask, commentMapper);
    }

    @Transactional
    public TaskResponse unsubscribeFromTask(String id, String userId) {
        log.info("unsubscribing from task: {}", id);
        Task task = findTaskById(id);
        if (task.getAssigneeIds() != null) {
            task.getAssigneeIds().remove(userId);
        }
        Task updatedTask = taskRepository.save(task);
        log.debug("Task unsubscribed with ID: {}", updatedTask.getId());
        kafkaProducerService.sendTaskUnsubscribedEvent(updatedTask.getId(), taskMapper.toTaskUnsubscribedEvent(updatedTask,userId));
        return taskMapper.toResponse(updatedTask, commentMapper);
    }

    @Transactional
    public TaskResponse completeTask(String id, String userId) {
        log.info("completing task: {}", id);
        Task task = findTaskById(id);
        task.setStatus(TaskStatus.COMPLETED);
        Task updatedTask = taskRepository.save(task);
        log.debug("Task completed with ID: {}", updatedTask.getId());
        kafkaProducerService.sendTaskCompletedEvent(updatedTask.getId(), taskMapper.toTaskCompletedEvent(updatedTask,userId));
        return taskMapper.toResponse(updatedTask, commentMapper);
    }

    public Page<TaskResponse> getMyTasks(String id, PageRequest of, String status, String department) {
        log.info("getting my tasks: {}", id);
        Page<Task> tasks = taskRepository.findByAssigneeIdsContains(id, of);
        return tasks.map(task -> taskMapper.toResponse(task, commentMapper));
    }

    public Page<TaskResponse> getAvailableTasks(PageRequest of, String department) {
        log.info("getting available tasks: {}", department);
        Department dep = parseEnum(department, Department.class);
        Page<Task> tasks = taskRepository.findByStatusAndDepartment(TaskStatus.AVAILABLE, dep, of);
        return tasks.map(task -> taskMapper.toResponse(task, commentMapper));
    }

    public Page<TaskResponse> getTasks(Pageable pageable, String status, String assigneeId, String creatorId, String department) {
        log.info("getting tasks: {}", pageable);
        TaskStatus st = parseEnum(status, TaskStatus.class);
        Department dep = parseEnum(department, Department.class);
        Page<Task> tasks = taskRepository.findTasksByFilters(st, assigneeId, creatorId, dep, pageable);
        return tasks.map(task -> taskMapper.toResponse(task, commentMapper));
    }

    public Page<TaskResponse> getDueSoonTasks(@Min(1) long hours, PageRequest of, String status, String assigneeId) {
        Instant now = Instant.now();
        Instant to = now.plus(Duration.ofHours(hours));

        Page<Task> tasks;
        if (status != null && !status.isBlank() && assigneeId != null && !assigneeId.isBlank()) {
            tasks = taskRepository.findByDueDateBetweenAndStatusAndAssigneeIds(now, to, TaskStatus.valueOf(status), assigneeId, of);
        } else if (status != null && !status.isBlank()) {
            tasks = taskRepository.findByDueDateBetweenAndStatus(now, to, TaskStatus.valueOf(status), of);
        } else if (assigneeId != null && !assigneeId.isBlank()) {
            tasks = taskRepository.findByDueDateBetweenAndAssigneeIds(now, to, assigneeId, of);
        } else {
            tasks = taskRepository.findByDueDateBetween(now, to, of);
        }

        return tasks.map(task -> taskMapper.toResponse(task, commentMapper));
    }

    @Transactional
    public void deleteTask(String id) {
        UserDetailsImpl details = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("soft-deleting (blocking) task: {}", id);
        Task task = findTaskById(id);
        task.setStatus(TaskStatus.BLOCKED);
        Task updatedTask = taskRepository.save(task);
        log.debug("Task soft-deleted (blocked) with ID: {}", id);
        kafkaProducerService.sendTaskDeletedEvent(id, taskMapper.toTaskDeletedEvent(updatedTask, details.getId()));
    }

    public Page<TaskResponse> getTasksByAssignee(String userId, PageRequest of, String status) {
        log.info("getting tasks by assignee: {}", userId);
        Page<Task> tasks;
        if (status == null || status.isBlank()) {
            tasks = taskRepository.findByAssigneeIdsContains(userId, of);
        } else {
            TaskStatus st = TaskStatus.valueOf(status);
            tasks = taskRepository.findByAssigneeIdsContainsAndStatus(userId, st, of);
        }
        return tasks.map(task -> taskMapper.toResponse(task, commentMapper));
    }

    @Transactional
    public TaskResponse updateStatus(String id, @Valid UpdateStatusRequest request) {
        log.info("updating status: {}", request);
        Task task = findTaskById(id);
        taskMapper.updateStatus(request, task);
        Task updatedTask = taskRepository.save(task);
        log.debug("Task status updated with ID: {}", updatedTask.getId());
        kafkaProducerService.sendTaskStatusUpdatedEvent(updatedTask.getId(), taskMapper.toTaskStatusUpdatedEvent(updatedTask));
        return taskMapper.toResponse(updatedTask, commentMapper);
    }

    @Transactional
    public List<TaskResponse> createBulkTasks(@Valid List<CreateTaskRequest> requests, String id) {
        log.info("creating bulk tasks: {}", requests);
        List<Task> tasks = requests.stream()
                .map(request -> taskMapper.toTask(request, id))
                .toList();
        List<Task> savedTasks = taskRepository.saveAll(tasks);
        log.debug("Tasks created with IDs: {}", savedTasks.stream().map(Task::getId).toList());
        savedTasks.forEach(task -> kafkaProducerService.sendTaskCreatedEvent(task.getId(), taskMapper.toTaskCreatedEvent(task)));
        return savedTasks.stream().map(task -> taskMapper.toResponse(task, commentMapper)).toList();
    }

    @Transactional
    public TaskResponse updateAssignees(String id, @Valid UpdateAssigneesRequest request) {
        log.info("updating assignees: {}", request);
        Task task = findTaskById(id);
        taskMapper.updateAssignees(request, task);
        Task updatedTask = taskRepository.save(task);
        log.debug("Task assignees updated with ID: {}", updatedTask.getId());
        kafkaProducerService.sendTaskAssigneesUpdatedEvent(updatedTask.getId(), taskMapper.toTaskAssigneesUpdatedEvent(updatedTask));
        return taskMapper.toResponse(updatedTask, commentMapper);
    }

    public Page<TaskResponse> getTaskHistory(String id, PageRequest of) {
        log.info("getting task history: {}", id);
        List<TaskStatus> statuses = List.of(TaskStatus.COMPLETED, TaskStatus.BLOCKED);
        Page<Task> tasks = taskRepository.findByAssigneeIdsContainsAndStatusIn(id, statuses, of);
        return tasks.map(task -> taskMapper.toResponse(task, commentMapper));
    }

    private static <E extends Enum<E>> E parseEnum(String value, Class<E> type) {
        if (value == null || value.isBlank()) return null;
        try {
            return Enum.valueOf(type, value);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

}

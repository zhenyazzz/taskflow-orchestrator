package org.example.taskservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleTaskNotFoundException(TaskNotFoundException ex) {
        log.warn("TaskNotFoundException: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return error;
    }

    @ExceptionHandler(CommentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleCommentNotFoundException(CommentNotFoundException ex) {
        log.warn("CommentNotFoundException: {}", ex.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
            log.warn("Validation error in field {}: {}", error.getField(), error.getDefaultMessage());
        });
        return errors;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        Map<String, String> error = new HashMap<>();
        error.put("error", "An unexpected error occurred: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}


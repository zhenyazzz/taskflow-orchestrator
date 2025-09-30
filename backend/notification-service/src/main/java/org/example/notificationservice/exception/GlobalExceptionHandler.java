package org.example.notificationservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<String> handleServiceUnavailableException(ServiceUnavailableException ex) {
        log.error("Service unavailable: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        return new ResponseEntity<>("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

package org.example.taskservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.example.taskservice.dto.response.attachment.AttachmentResponse;
import org.example.taskservice.service.AttachmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/attachments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Attachments", description = "Attachment management operations")
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PostMapping("/task/{taskId}/batch")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<AttachmentResponse>> addAttachments(
            @PathVariable String taskId,
            @RequestPart("files") List<MultipartFile> files) {
        log.info("addAttachments taskId={}, filesCount={}", taskId, files.size());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(attachmentService.addAttachments(taskId, files));
    }
    

    @GetMapping("/task/{taskId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<AttachmentResponse>> getAttachments(@PathVariable String taskId) {
        log.info("getAttachments taskId={}", taskId);
        return ResponseEntity.ok(attachmentService.getAttachments(taskId));
    }

    @DeleteMapping("/task/{taskId}/{attachmentIds}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAttachment(@PathVariable String taskId, @PathVariable List<String> attachmentIds) {
        log.info("deleteAttachment taskId={}, attachmentId={}", taskId, attachmentIds);
        attachmentService.deleteAttachment(taskId, attachmentIds);
    }

}

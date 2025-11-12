package org.example.taskservice.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.example.taskservice.dto.response.attachment.AttachmentResponse;
import org.example.taskservice.mapper.AttachmentMapper;
import org.example.taskservice.model.Attachment;
import org.example.taskservice.model.UserDetailsImpl;
import org.example.taskservice.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.example.taskservice.kafka.producer.KafkaProducerService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentService {
    private final MinioClient minioClient;
    @Value("${minio.bucket-name}")
    private String bucketName;

    private final AttachmentMapper attachmentMapper;
    private final AttachmentRepository attachmentRepository;
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public List<AttachmentResponse> addAttachments(String taskId, List<MultipartFile> files) {
        List<AttachmentResponse> attachmentResponses = new ArrayList<>();
        log.info("Adding attachments to task: {}", taskId);
        try {
            ensureBucketExists();
            for (MultipartFile file : files) {
                String objectName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build());
                Attachment attachment = new Attachment();
                attachment.setTaskId(taskId);
                attachment.setFileName(file.getOriginalFilename());
                attachment.setObjectName(objectName);
                attachment.setUrl(genereateUrl(objectName));
                attachment.setSize(file.getSize());
                attachment.setFileType(file.getContentType());
                attachment.setCreatedAt(Instant.now());

                Attachment savedAttachment = attachmentRepository.save(attachment);

                attachmentResponses.add(attachmentMapper.toResponse(savedAttachment));
                kafkaProducerService.sendAttachmentAddedEvent(
                        savedAttachment.getId(),
                        attachmentMapper.toAttachmentAddedEvent(savedAttachment, getCurrentUserId())
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to add attachments", e);
        }
        return attachmentResponses;
    }

    public String genereateUrl(String objectName) {
        try {
            ensureBucketExists();
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .method(Method.GET)
                    .expiry(60 * 60)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate url", e);
        }
    }

    public List<AttachmentResponse> getAttachments(String taskId) {
        log.info("Getting attachments for task: {}", taskId);
        List<Attachment> attachments = attachmentRepository.findByTaskId(taskId);
        return attachments.stream()
                .map(attachment -> {
                    attachment.setUrl(genereateUrl(attachment.getObjectName()));
                    return attachmentMapper.toResponse(attachment);
                })
                .toList();
    }

    @Transactional
    public void deleteAttachment(String taskId, List<String> attachmentIds) {
        log.info("Deleting attachments for task: {}", taskId);
        for (String attachmentId : attachmentIds) {
            Attachment attachment = getAttachment(attachmentId);
            if (!attachment.getTaskId().equals(taskId)) {
                throw new RuntimeException("Attachment does not belong to this task");
            }
            try {
                ensureBucketExists();
                minioClient.removeObject(io.minio.RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(attachment.getObjectName())
                        .build());
            } catch (Exception e) {
                throw new RuntimeException("Failed to delete file from storage", e);
            }
            attachmentRepository.deleteById(attachmentId);
            kafkaProducerService.sendAttachmentDeletedEvent(attachmentId, attachmentMapper.toAttachmentDeletedEvent(attachment, getCurrentUserId()));
        }
    }   

    public Attachment getAttachment(String attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));
    }

    public String getCurrentUserId() {
        UserDetailsImpl details = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Current user id: {}", details.getId());
        return details.getId();
    }

    private void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );

            if (!exists) {
                log.warn("MinIO bucket '{}' not found. Attempting to create…", bucketName);
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("MinIO bucket '{}' created successfully", bucketName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to ensure MinIO bucket existence", e);
        }
    }
}

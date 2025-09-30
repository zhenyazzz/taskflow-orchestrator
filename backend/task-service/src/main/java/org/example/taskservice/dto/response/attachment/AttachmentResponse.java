package org.example.taskservice.dto.response.attachment;

import java.time.Instant;

public record AttachmentResponse(
    String id,
    String taskId,
    String fileName,
    String objectName,
    Long size,
    String url,
    String fileType,
    Instant createdAt
) {

}

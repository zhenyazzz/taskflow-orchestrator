package org.example.events.attachment;

import java.time.Instant;

public record AttachmentAddedEvent(
    String id,
    String taskId,
    String fileName,
    String fileType,
    Long size,
    String uploadedBy,
    Instant timestamp
) {
    
}

package org.example.events.attachment;

import java.time.Instant;

public record AttachmentDeletedEvent(
    String id,
    String taskId,
    String fileName,
    String fileType,
    Long size,
    String deletedBy,
    Instant timestamp
) {

}

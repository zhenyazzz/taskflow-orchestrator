package org.example.taskservice.model;

import java.time.Instant;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
@Document(collection = "attachments")
public class Attachment {

    @Id
    private String id;

    private String taskId;

    private String fileName;

    private String objectName;
    
    private Long size;

    private String url;
    
    private String fileType;

    @CreatedDate
    private Instant createdAt;
}

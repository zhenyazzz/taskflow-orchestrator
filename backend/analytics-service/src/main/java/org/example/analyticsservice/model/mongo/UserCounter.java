package org.example.analyticsservice.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_counts")
public class UserCounter {

    @Id
    private String id;

    @Builder.Default
    @Field("total_users")
    private Long totalUsers = 0L;

    @Builder.Default
    @Field("last_updated")
    private Instant lastUpdated = Instant.now();
}

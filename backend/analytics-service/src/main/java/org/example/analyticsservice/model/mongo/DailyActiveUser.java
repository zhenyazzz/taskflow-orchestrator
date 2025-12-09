package org.example.analyticsservice.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.UUID;

@Document(collection = "daily_active_users")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyActiveUser {
    @Id
    private String id;

    @Field("username")
    private String username;

    @Field("date")
    @Builder.Default
    private LocalDate date = LocalDate.now();

    @Field("login_count")
    @Builder.Default
    private Long loginCount = 0L;
}

package org.example.analyticsservice.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_statistics")
public class UserStatistics {

    @Id
    private String id;

    @Field("date")
    @Builder.Default
    private LocalDate date = LocalDate.now();

    @Builder.Default
    @Field("total_users")
    private Long totalUsers = 0L;

    @Field("new_users_today")
    @Builder.Default
    private Long newUsersToday = 0L;

    @Field("active_users_today")
    @Builder.Default
    private Long activeUsersToday = 0L;

    @Field("successful_logins")
    @Builder.Default
    private Long successfulLogins = 0L;

    @Field("failed_logins")
    @Builder.Default
    private Long failedLogins = 0L;

    @Field("last_updated")
    @Builder.Default
    private Instant lastUpdated = Instant.now();
}
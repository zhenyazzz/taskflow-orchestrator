package org.example.analyticsservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



import java.time.Instant;

import org.example.events.enums.Role;
import org.example.events.enums.Department;

@Entity
@Table(name = "user_metrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "event_type", nullable = false)
    private UserEventType eventType; 

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "department")
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "event_timestamp", nullable = false)
    private Instant eventTimestamp;

    @PrePersist
    protected void onCreate() {
        eventTimestamp = Instant.now();
    }
}

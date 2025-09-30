package org.example.analyticsservice.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "login_metrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "login_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginStatus loginStatus; 

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "event_timestamp", nullable = false)
    private Instant eventTimestamp;

    @PrePersist
    protected void onCreate() {
        eventTimestamp = Instant.now();
    }
}

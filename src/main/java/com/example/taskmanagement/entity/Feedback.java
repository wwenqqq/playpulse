package com.example.taskmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "feedback")
public class Feedback {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(nullable = false, length = 160) private String title;
    @Column(nullable = false, columnDefinition = "TEXT") private String description;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Category category;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Status status = Status.REPORTED;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Priority priority = Priority.MEDIUM;
    @Column(name = "game_version", length = 30) private String gameVersion;
    @CreationTimestamp @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at") private LocalDateTime updatedAt;
    public enum Category { BUG, BALANCE, FEATURE }
    public enum Status { REPORTED, TRIAGED, IN_PROGRESS, FIXED }
    public enum Priority { LOW, MEDIUM, HIGH, CRITICAL }
}

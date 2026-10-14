package com.example.taskmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "game")
public class Game {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 120) private String title;
    @Column(nullable = false, length = 90) private String studio;
    @Column(nullable = false) private Integer releaseYear;
    @Column(nullable = false, length = 120) private String genres;
    @Column(nullable = false, length = 24) private String coverTone;
    @Column(nullable = false, columnDefinition = "TEXT") private String summary;
    @Column(name = "created_by") private Long createdBy;
    @CreationTimestamp @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
}

package com.example.taskmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "library_entry", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "game_id"}))
public class LibraryEntry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "game_id", nullable = false) private Long gameId;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Status status = Status.WISHLIST;
    @CreationTimestamp @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    public enum Status { WISHLIST, PLAYING, COMPLETED }
}

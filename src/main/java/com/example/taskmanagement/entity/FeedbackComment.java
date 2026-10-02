package com.example.taskmanagement.entity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
@Data @Entity @Table(name = "feedback_comment")
public class FeedbackComment { @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; @Column(name="feedback_id",nullable=false) private Long feedbackId; @Column(name="user_id",nullable=false) private Long userId; @Column(nullable=false,columnDefinition="TEXT") private String content; @CreationTimestamp @Column(name="created_at",updatable=false) private LocalDateTime createdAt; }

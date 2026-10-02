package com.example.taskmanagement.entity;
import jakarta.persistence.*;
import lombok.Data;
@Data @Entity @Table(name = "feedback_vote", uniqueConstraints = @UniqueConstraint(columnNames = {"feedback_id", "user_id"}))
public class FeedbackVote { @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; @Column(name="feedback_id",nullable=false) private Long feedbackId; @Column(name="user_id",nullable=false) private Long userId; }

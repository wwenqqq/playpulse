package com.example.taskmanagement.dto;
import com.example.taskmanagement.entity.Feedback;
import lombok.Data;
import java.time.LocalDateTime;
@Data public class FeedbackView { private Long id,userId; private String title,description,gameVersion; private Feedback.Category category; private Feedback.Status status; private Feedback.Priority priority; private LocalDateTime createdAt,updatedAt; private long voteCount,commentCount; }

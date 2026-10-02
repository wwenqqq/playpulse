package com.example.taskmanagement.dto;
import com.example.taskmanagement.entity.Feedback;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data public class FeedbackCreateDTO { @NotBlank @Size(max=160) private String title; @NotBlank private String description; @NotNull private Feedback.Category category; private Feedback.Priority priority=Feedback.Priority.MEDIUM; @Size(max=30) private String gameVersion; }

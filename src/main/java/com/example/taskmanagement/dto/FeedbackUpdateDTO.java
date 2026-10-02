package com.example.taskmanagement.dto;
import com.example.taskmanagement.entity.Feedback;
import lombok.Data;
@Data public class FeedbackUpdateDTO { private String title; private String description; private Feedback.Category category; private Feedback.Priority priority; private Feedback.Status status; private String gameVersion; }

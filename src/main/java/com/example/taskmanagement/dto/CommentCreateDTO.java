package com.example.taskmanagement.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data public class CommentCreateDTO { @NotBlank @Size(max=1000) private String content; }

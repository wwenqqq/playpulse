package com.example.taskmanagement.dto;

import com.example.taskmanagement.entity.Task;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskUpdateDTO {

    private String title;

    private String description;

    private Task.TaskStatus status;

    // 新增：截止时间
    private LocalDateTime deadline;
}
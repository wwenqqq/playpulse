package com.example.taskmanagement.controller;

import com.example.taskmanagement.annotation.LogOperation;
import com.example.taskmanagement.dto.Result;
import com.example.taskmanagement.dto.TaskCreateDTO;
import com.example.taskmanagement.dto.TaskUpdateDTO;
import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.service.TaskService;
import com.example.taskmanagement.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "legacy.tasks.enabled", havingValue = "true")
public class TaskController {

    private final TaskService taskService;
    private final JwtUtil jwtUtil;

    // 从请求头获取用户ID
    private Long getUserIdFromHeader(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new RuntimeException("未登录或token无效");
        }
        String token = authorization.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("token无效或已过期");
        }
        return jwtUtil.getUserIdFromToken(token);
    }

    // 创建任务
    @LogOperation("创建任务")
    @PostMapping
    public Result<Task> createTask(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody TaskCreateDTO dto) {
        Long userId = getUserIdFromHeader(authorization);
        Task task = taskService.createTask(userId, dto);
        return Result.success("任务创建成功", task);
    }

    // 分页查询任务列表（支持按截止时间排序）
    @LogOperation("查询任务列表")
    @GetMapping
    public Result<Page<Task>> getTaskList(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        Long userId = getUserIdFromHeader(authorization);
        Page<Task> tasks = taskService.getTaskList(userId, page, size, sortBy);
        return Result.success(tasks);
    }

    // 查询单个任务
    @LogOperation("查询任务详情")
    @GetMapping("/{id}")
    public Result<Task> getTaskById(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id) {
        Long userId = getUserIdFromHeader(authorization);
        Task task = taskService.getTaskById(userId, id);
        return Result.success(task);
    }

    // 更新任务
    @LogOperation("更新任务")
    @PutMapping("/{id}")
    public Result<Task> updateTask(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id,
            @RequestBody TaskUpdateDTO dto) {
        Long userId = getUserIdFromHeader(authorization);
        Task task = taskService.updateTask(userId, id, dto);
        return Result.success("任务更新成功", task);
    }

    // 删除任务
    @LogOperation("删除任务")
    @DeleteMapping("/{id}")
    public Result<String> deleteTask(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id) {
        Long userId = getUserIdFromHeader(authorization);
        taskService.deleteTask(userId, id);
        return Result.success("任务删除成功");
    }
}

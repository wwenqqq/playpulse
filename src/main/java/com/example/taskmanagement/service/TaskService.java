package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.TaskCreateDTO;
import com.example.taskmanagement.dto.TaskUpdateDTO;
import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String TASK_CACHE_PREFIX = "task:";
    private static final long CACHE_EXPIRE_TIME = 10; // 10分钟

    // 创建任务
    @Transactional
    public Task createTask(Long userId, TaskCreateDTO dto) {
        Task task = new Task();
        task.setUserId(userId);
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(Task.TaskStatus.TODO);
        task.setDeadline(dto.getDeadline()); // 设置截止时间

        return taskRepository.save(task);
    }

    // 分页查询任务列表（支持按截止时间排序）
    public Page<Task> getTaskList(Long userId, int page, int size, String sortBy) {
        Sort sort;

        // 根据排序字段创建排序对象
        if ("deadline".equals(sortBy)) {
            // 按截止时间升序（最近的在前面）
            sort = Sort.by(Sort.Direction.ASC, "deadline")
                    .and(Sort.by(Sort.Direction.DESC, "createdAt"));
        } else if ("createdAt".equals(sortBy)) {
            // 按创建时间降序
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        } else {
            // 默认按创建时间降序
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        return taskRepository.findByUserId(userId, pageable);
    }

    // 查询单个任务（带Redis缓存）
    public Task getTaskById(Long userId, Long taskId) {
        String cacheKey = TASK_CACHE_PREFIX + taskId;

        // 先从Redis查询
        Task cachedTask = (Task) redisTemplate.opsForValue().get(cacheKey);
        if (cachedTask != null) {
            System.out.println("✅ 从缓存中获取任务: " + taskId);
            return cachedTask;
        }

        // 缓存未命中，从数据库查询
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        // 写入Redis缓存
        redisTemplate.opsForValue().set(cacheKey, task, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        System.out.println("📝 任务已缓存: " + taskId);

        return task;
    }

    // 更新任务
    @Transactional
    public Task updateTask(Long userId, Long taskId, TaskUpdateDTO dto) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        if (dto.getTitle() != null) {
            task.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            task.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            task.setStatus(dto.getStatus());
        }
        if (dto.getDeadline() != null) {
            task.setDeadline(dto.getDeadline()); // 更新截止时间
        }

        Task updatedTask = taskRepository.save(task);

        // 删除缓存
        String cacheKey = TASK_CACHE_PREFIX + taskId;
        redisTemplate.delete(cacheKey);
        System.out.println("🗑️ 缓存已删除: " + taskId);

        return updatedTask;
    }

    // 删除任务
    @Transactional
    public void deleteTask(Long userId, Long taskId) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        taskRepository.delete(task);

        // 删除缓存
        String cacheKey = TASK_CACHE_PREFIX + taskId;
        redisTemplate.delete(cacheKey);
        System.out.println("🗑️ 任务和缓存已删除: " + taskId);
    }
}
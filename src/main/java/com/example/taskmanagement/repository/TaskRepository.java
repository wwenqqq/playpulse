package com.example.taskmanagement.repository;

import com.example.taskmanagement.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // 分页查询某个用户的所有任务
    Page<Task> findByUserId(Long userId, Pageable pageable);

    // 查找某个用户的某个任务
    Optional<Task> findByIdAndUserId(Long id, Long userId);

    // 删除某个用户的某个任务
    void deleteByIdAndUserId(Long id, Long userId);
}
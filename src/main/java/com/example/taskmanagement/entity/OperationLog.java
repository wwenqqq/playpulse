package com.example.taskmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "operation_log")
public class OperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", length = 50)
    private String username;

    @Column(name = "operation", length = 100)
    private String operation; // 操作类型

    @Column(name = "method", length = 200)
    private String method; // 方法名

    @Column(name = "params", columnDefinition = "TEXT")
    private String params; // 请求参数

    @Column(name = "result", columnDefinition = "TEXT")
    private String result; // 返回结果

    @Column(name = "ip", length = 50)
    private String ip; // IP地址

    @Column(name = "execute_time")
    private Long executeTime; // 执行时间（毫秒）

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
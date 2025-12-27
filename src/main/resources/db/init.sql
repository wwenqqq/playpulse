-- ========================================
-- 任务管理系统数据库初始化脚本
-- ========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS task_management
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE task_management;

-- ========================================
-- 用户表
-- ========================================
CREATE TABLE IF NOT EXISTS `user` (
                                      `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                      `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（MD5加密）',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ========================================
-- 任务表
-- ========================================
CREATE TABLE IF NOT EXISTS `task` (
                                      `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                      `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                      `title` VARCHAR(200) NOT NULL COMMENT '任务标题',
    `description` TEXT COMMENT '任务描述',
    `status` VARCHAR(20) NOT NULL DEFAULT 'TODO' COMMENT '状态：TODO/DOING/DONE',
    `deadline` DATETIME DEFAULT NULL COMMENT '截止时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_deadline` (`deadline`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务表';

-- ========================================
-- 操作日志表
-- ========================================
CREATE TABLE IF NOT EXISTS `operation_log` (
                                               `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                               `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
                                               `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名',
    `operation` VARCHAR(100) DEFAULT NULL COMMENT '操作类型',
    `method` VARCHAR(200) DEFAULT NULL COMMENT '方法名',
    `params` TEXT DEFAULT NULL COMMENT '请求参数',
    `result` TEXT DEFAULT NULL COMMENT '返回结果',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `execute_time` BIGINT DEFAULT NULL COMMENT '执行时间（毫秒）',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_operation` (`operation`),
    KEY `idx_created_at` (`created_at`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ========================================
-- 插入测试数据（可选）
-- ========================================

-- 插入测试用户（密码是 123456 的 MD5：e10adc3949ba59abbe56e057f20f883e）
INSERT INTO `user` (`username`, `password`) VALUES
                                                ('testuser', 'e10adc3949ba59abbe56e057f20f883e'),
                                                ('admin', 'e10adc3949ba59abbe56e057f20f883e')
    ON DUPLICATE KEY UPDATE username=username;

-- 插入测试任务
INSERT INTO `task` (`user_id`, `title`, `description`, `status`, `deadline`) VALUES
                                                                                 (1, '完成项目文档', '编写项目README和API文档', 'TODO', '2025-12-31 23:59:59'),
                                                                                 (1, '代码review', '审查新功能代码', 'DOING', '2025-12-28 18:00:00'),
                                                                                 (1, '学习Spring Boot', '深入学习Spring Boot框架', 'TODO', NULL)
    ON DUPLICATE KEY UPDATE title=title;

-- ========================================
-- 查看表结构
-- ========================================
SHOW TABLES;
DESCRIBE `user`;
DESCRIBE `task`;
DESCRIBE `operation_log`;

-- ========================================
-- 说明
-- ========================================
-- 1. 本脚本会自动创建数据库和所有表
-- 2. 如果表已存在，不会重复创建
-- 3. 测试数据可选，包含2个用户和3个任务
-- 4. 所有用户默认密码：123456
-- 5. JPA 会自动创建表，此脚本仅供参考
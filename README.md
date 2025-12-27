# 任务管理系统 (Task Management System)

基于 Spring Boot 3.2.1 + MySQL + Redis 的任务管理系统，实现用户认证、任务管理、Redis缓存、操作日志等完整功能。

## ✨ 功能特性

### 基础功能
- ✅ 用户注册/登录（JWT Token 认证，30天有效期）
- ✅ 任务增删改查（支持截止时间设置）
- ✅ 分页查询任务列表（支持按创建时间、截止时间排序）
- ✅ Redis 缓存优化（查询单个任务时自动缓存，10分钟过期）
- ✅ 任务状态管理（TODO/DOING/DONE）

### 扩展功能
- ✅ **登录失败限制**：5次失败锁定30分钟，防暴力破解
- ✅ **AOP 操作日志**：自动记录所有关键操作（用户、参数、结果、执行时间、IP）
- ✅ **任务截止时间**：支持设置和更新截止时间，按截止时间排序

---

## 🛠️ 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.1 | 核心框架 |
| Java | 21 | 编程语言 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 5.0+ | 缓存数据库 |
| Spring Data JPA | 3.2.1 | ORM 框架 |
| JWT | 0.12.5 | Token 认证 |
| Spring AOP | - | 切面编程（日志记录） |
| Lombok | - | 简化代码 |
| Maven | 3.6+ | 构建工具 |

---

## 📌 环境要求

- JDK 21+
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+
- Postman（用于 API 测试）

---

## 🚀 快速开始

### 1. 创建数据库
```sql
CREATE DATABASE task_management 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

**执行初始化脚本（可选）：**
```bash
mysql -u root -p task_management < src/main/resources/db/init.sql
```

> 💡 项目使用 JPA 自动建表，也可以不执行脚本。

---

### 2. 配置数据库连接

**编辑 `src/main/resources/application.yml`：**
```yaml
spring:
  datasource:
    username: root
    password: 你的MySQL密码  # ⚠️ 修改这里
  
  data:
    redis:
      host: localhost
      port: 6379
```

---

### 3. 启动 Redis

**Windows:**
```bash
cd "C:\Program Files\Redis"
redis-server.exe
```

**Mac/Linux:**
```bash
redis-server
```

**验证启动：**
```bash
redis-cli ping
# 返回 PONG 说明成功
```

---

### 4. 启动项目

**方式一：使用 Maven**
```bash
mvn spring-boot:run
```

**方式二：使用 IDE**
- 运行 `TaskmanagementApplication.java`

**方式三：打包运行**
```bash
mvn clean package
java -jar target/taskmanagement-0.0.1-SNAPSHOT.jar
```

---

### 5. 验证启动成功

**控制台看到：**
```
Started TaskmanagementApplication in X.XXX seconds
Tomcat started on port 8080 (http)
```

**访问 API 文档（可选）：**
```
http://localhost:8080/doc.html
```

---

## 📮 API 测试

### 快速测试流程

**1. 用户注册**
```bash
POST http://localhost:8080/api/auth/register

Body:
{
  "username": "testuser",
  "password": "123456"
}
```

**2. 用户登录（获取 Token）**
```bash
POST http://localhost:8080/api/auth/login

Body:
{
  "username": "testuser",
  "password": "123456"
}

Response:
{
  "token": "eyJhbGci...",  // 复制这个 token
  "userId": 1,
  "username": "testuser"
}
```

**3. 创建任务（需要 Token）**
```bash
POST http://localhost:8080/api/tasks

Headers:
Authorization: Bearer 你的token

Body:
{
  "title": "完成项目报告",
  "description": "周五截止",
  "deadline": "2025-12-31T23:59:59"
}
```

**4. 查询任务列表**
```bash
GET http://localhost:8080/api/tasks?page=0&size=10&sortBy=deadline

Headers:
Authorization: Bearer 你的token
```

**完整 API 文档和示例：** 📄 [API_EXAMPLES.md](API_EXAMPLES.md)

---

## 📖 API 概览

### 用户相关

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 用户注册 | POST | /api/auth/register | 注册新用户 |
| 用户登录 | POST | /api/auth/login | 登录获取 Token |
| 获取用户信息 | GET | /api/auth/info | 获取当前用户信息 |

### 任务相关

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 创建任务 | POST | /api/tasks | 创建新任务 |
| 查询任务列表 | GET | /api/tasks | 分页查询（支持排序） |
| 查询单个任务 | GET | /api/tasks/{id} | 查询任务详情（Redis缓存） |
| 更新任务 | PUT | /api/tasks/{id} | 更新任务信息 |
| 删除任务 | DELETE | /api/tasks/{id} | 删除任务 |

> 💡 所有任务相关接口都需要在 Header 中添加：`Authorization: Bearer {token}`

---

## 📁 项目结构
```
taskmanagement/
├── src/main/java/com/example/taskmanagement/
│   ├── annotation/          # 自定义注解（@LogOperation）
│   ├── aspect/              # AOP 切面（操作日志记录）
│   ├── config/              # 配置类（Redis）
│   ├── controller/          # 控制器层（User、Task）
│   ├── dto/                 # 数据传输对象
│   ├── entity/              # 实体类（User、Task、OperationLog）
│   ├── exception/           # 全局异常处理
│   ├── repository/          # 数据访问层（JPA）
│   ├── service/             # 业务逻辑层
│   └── util/                # 工具类（JWT、密码加密）
├── src/main/resources/
│   ├── application.yml      # 配置文件
│   └── db/init.sql          # 数据库初始化脚本
├── pom.xml                  # Maven 配置
├── README.md                # 项目说明（本文件）
└── API_EXAMPLES.md          # API 详细示例
```

---

## 🗄️ 数据库设计

### 核心表

**用户表 (user)**
- 主键：id
- 唯一索引：username
- 字段：username, password(MD5), created_at, updated_at

**任务表 (task)**
- 主键：id
- 索引：user_id, status, deadline
- 字段：user_id, title, description, status, deadline, created_at, updated_at

**操作日志表 (operation_log)**
- 主键：id
- 索引：user_id, operation, created_at
- 字段：user_id, username, operation, method, params, result, ip, execute_time, created_at

---

## 💡 核心功能说明

### 1. JWT 认证
- Token 有效期：30天
- 格式：`Authorization: Bearer {token}`
- Token 包含：用户ID、用户名、签发时间、过期时间

### 2. Redis 缓存
- **Key 格式：** `task:{taskId}`
- **过期时间：** 10分钟
- **缓存场景：** 查询单个任务详情
- **缓存更新：** 更新/删除任务时自动清除缓存

### 3. 登录失败限制
- **最多失败：** 5次
- **锁定时间：** 30分钟
- **Redis Key：** `login:fail:{username}`
- **自动解锁：** 30分钟后或成功登录

### 4. AOP 操作日志
- **记录内容：** 用户信息、操作类型、请求参数、返回结果、执行时间、IP地址
- **查询日志：**
```sql
  SELECT * FROM operation_log ORDER BY created_at DESC;
```

---

## ❓ 常见问题

### Q: Token 无效或已过期？
**A:** 重新登录获取新 Token，注意格式：`Bearer {token}`（Bearer 后有空格）

### Q: Redis 连接失败？
**A:** 确保 Redis 已启动，执行 `redis-cli ping` 验证

### Q: 数据库连接失败？
**A:** 检查 MySQL 是否启动，确认 application.yml 中的用户名密码

### Q: 登录失败5次被锁定怎么办？
**A:** 等待30分钟自动解锁，或在 Redis 中删除：`DEL login:fail:testuser`

---

## 📄 许可证

MIT License

---

## 📞 联系方式

- **GitHub:** [wwenqqq/taskmanagement](https://github.com/wwenqqq/taskmanagement)
---

**最后更新：** 2025-12-27
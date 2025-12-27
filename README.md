# 任务管理系统 (Task Management System)

基于 Spring Boot 3.2.1 + MySQL + Redis 的任务管理系统，实现用户认证、任务管理、Redis缓存、操作日志等完整功能。

## 📋 目录

- [功能特性](#功能特性)
- [技术栈](#技术栈)
- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [使用 Postman 测试](#使用-postman-测试)
- [项目结构](#项目结构)
- [数据库设计](#数据库设计)
- [核心功能说明](#核心功能说明)
- [常见问题](#常见问题)

---

## ✨ 功能特性

### 基础功能

#### 用户模块
- ✅ 用户注册（用户名唯一性校验、密码 MD5 加密存储）
- ✅ 用户登录（JWT Token 认证，30天有效期）
- ✅ 获取用户信息
- ✅ 登录失败次数限制（5次失败锁定30分钟，防暴力破解）

#### 任务模块
- ✅ 创建任务（支持设置截止时间）
- ✅ 分页查询任务列表（每页10条，可自定义）
- ✅ 多种排序方式（按创建时间、截止时间排序）
- ✅ 查询单个任务详情（Redis 缓存优化，10分钟过期）
- ✅ 更新任务（标题、描述、状态、截止时间）
- ✅ 删除任务（同时删除缓存）
- ✅ 任务状态管理（TODO/DOING/DONE）

### 扩展功能

#### 截止时间管理
- ✅ 任务支持设置截止时间
- ✅ 按截止时间排序（最近截止的排在前面）
- ✅ 支持更新截止时间

#### 安全防护
- ✅ Redis 实现登录失败次数限制
- ✅ 最多失败5次，锁定30分钟
- ✅ 成功登录自动清除失败记录
- ✅ 不同用户独立计数

#### 操作日志
- ✅ AOP 切面自动记录所有关键操作
- ✅ 记录用户信息、操作类型、请求参数、返回结果
- ✅ 记录执行时间、IP地址
- ✅ 异常情况也会记录

### 技术亮点

- ✅ **Redis 缓存策略**：查询单个任务时自动缓存，更新/删除时自动失效
- ✅ **统一异常处理**：全局异常拦截，返回友好错误信息
- ✅ **统一返回结果**：Result<T> 封装，code/message/data 统一格式
- ✅ **参数校验**：使用 Validation 注解自动校验参数
- ✅ **RESTful API 设计**：符合 REST 规范
- ✅ **JWT 无状态认证**：Token 存储用户信息，无需 Session
- ✅ **AOP 日志记录**：自动记录操作日志，无侵入业务代码

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
| Lombok | - | 简化代码 |
| Validation | - | 参数校验 |
| Spring AOP | - | 切面编程 |
| Knife4j | 4.5.0 | API 文档（可选） |
| Maven | 3.6+ | 构建工具 |

---

## 📌 环境要求

- **JDK**: 21 或更高版本
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Redis**: 5.0+
- **Postman**: 最新版（用于 API 测试）
- **IDE**: IntelliJ IDEA（推荐）或 Eclipse

---

## 🚀 快速开始

### 1. 克隆项目
```bash
git clone <https://github.com/wwenqqq/taskmanagement.git>
cd taskmanagement
```

### 2. 创建数据库

**打开 MySQL 客户端，执行：**
```sql
CREATE DATABASE task_management 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

**执行初始化脚本（可选）：**

项目使用 JPA 自动建表，但也提供了 SQL 脚本：
```bash
mysql -u root -p task_management < src/main/resources/db/init.sql
```

### 3. 配置数据库连接

**编辑 `src/main/resources/application.yml`：**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/task_management?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: 你的MySQL密码  # ⚠️ 修改这里
  
  data:
    redis:
      host: localhost
      port: 6379
```

### 4. 启动 Redis

**Windows:**
```bash
cd "C:\Program Files\Redis"
redis-server.exe
```

**Mac/Linux:**
```bash
redis-server
```

**验证 Redis 启动：**
```bash
redis-cli ping
# 返回 PONG 说明成功
```

### 5. 启动项目

**方式一：使用 Maven**
```bash
mvn spring-boot:run
```

**方式二：使用 IDE**
1. 打开 IntelliJ IDEA
2. 导入项目
3. 找到 `TaskmanagementApplication.java`
4. 右键 → Run

**方式三：打包运行**
```bash
mvn clean package
java -jar target/taskmanagement-0.0.1-SNAPSHOT.jar
```

### 6. 验证启动成功

**控制台看到以下信息说明启动成功：**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.1)

...
Started TaskmanagementApplication in X.XXX seconds
Tomcat started on port 8080 (http)
```

**可选：访问 API 文档（如果安装了 Knife4j）：**
```
http://localhost:8080/doc.html
```

---

## 📮 使用 Postman 测试

### 准备工作

**1. 下载安装 Postman**
- 官网下载：https://www.postman.com/downloads/
- 或使用网页版：https://web.postman.com/

**2. 创建 Collection**
- 打开 Postman
- 点击左侧 **Collections**
- 点击 **+** 创建新 Collection
- 命名为：`Task Management System`

---

### 测试流程

#### 📝 第一步：用户注册

**创建新请求：**
```
方法：POST
URL：http://localhost:8080/api/auth/register
```

**设置 Headers：**
```
Content-Type: application/json
```

**设置 Body（选择 raw → JSON）：**
```json
{
  "username": "testuser",
  "password": "123456"
}
```

**点击 Send**

**预期响应（成功）：**
```json
{
    "code": 200,
    "message": "注册成功",
    "data": {
        "id": 1,
        "username": "testuser",
        "password": null,
        "createdAt": "2025-12-27T23:00:00",
        "updatedAt": "2025-12-27T23:00:00"
    }
}
```

✅ **注册成功！**

---

#### 🔐 第二步：用户登录（获取 Token）

**创建新请求：**
```
方法：POST
URL：http://localhost:8080/api/auth/login
```

**设置 Headers：**
```
Content-Type: application/json
```

**设置 Body：**
```json
{
  "username": "testuser",
  "password": "123456"
}
```

**点击 Send**

**预期响应：**
```json
{
    "code": 200,
    "message": "登录成功",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwidXNlcm5hbWUiOiJ0ZXN0dXNlciIsImlhdCI6MTczNTMxNDAwMCwiZXhwIjoxNzM3OTA2MDAwfQ.example_token_here",
        "userId": 1,
        "username": "testuser"
    }
}
```

**⚠️ 重要：复制 `token` 的值！**

例如：`eyJhbGciOiJIUzI1NiJ9...`

---

#### 🔑 设置全局 Token（推荐）

**方式一：Collection 级别设置（推荐）**

1. 点击 Collection `Task Management System`
2. 点击 **Authorization** 标签
3. Type 选择：**Bearer Token**
4. Token 输入框粘贴你的 token
5. 保存

**所有请求继承认证：**
- 在每个新请求中
- Authorization Type 选择：**Inherit auth from parent**

**方式二：每个请求单独设置**

在每个需要认证的请求中：
1. 点击 **Headers** 标签
2. 添加：`Authorization: Bearer 你的token`

---

#### ✏️ 第三步：创建任务

**创建新请求：**
```
方法：POST
URL：http://localhost:8080/api/tasks
```

**设置 Headers：**
```
Content-Type: application/json
Authorization: Bearer 你的token
```

**设置 Body：**
```json
{
  "title": "完成项目报告",
  "description": "需要在本周五前完成",
  "deadline": "2025-12-31T23:59:59"
}
```

**点击 Send**

**预期响应：**
```json
{
    "code": 200,
    "message": "任务创建成功",
    "data": {
        "id": 1,
        "userId": 1,
        "title": "完成项目报告",
        "description": "需要在本周五前完成",
        "status": "TODO",
        "deadline": "2025-12-31T23:59:59",
        "createdAt": "2025-12-27T23:05:00",
        "updatedAt": "2025-12-27T23:05:00"
    }
}
```

✅ **任务创建成功！可以看到 `deadline` 字段**

---

#### 📋 第四步：查询任务列表

**创建新请求：**
```
方法：GET
URL：http://localhost:8080/api/tasks?page=0&size=10&sortBy=deadline
```

**或在 Params 标签设置：**
- `page`: 0
- `size`: 10
- `sortBy`: deadline

**设置 Headers：**
```
Authorization: Bearer 你的token
```

**⚠️ 注意：GET 请求 Body 选择 `none`**

**点击 Send**

**预期响应：**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "content": [
            {
                "id": 1,
                "userId": 1,
                "title": "完成项目报告",
                "description": "需要在本周五前完成",
                "status": "TODO",
                "deadline": "2025-12-31T23:59:59",
                "createdAt": "2025-12-27T23:05:00",
                "updatedAt": "2025-12-27T23:05:00"
            }
        ],
        "totalElements": 1,
        "totalPages": 1,
        "size": 10,
        "number": 0
    }
}
```

**参数说明：**
- `page`: 页码（从0开始）
- `size`: 每页数量
- `sortBy`: 排序字段
    - `deadline`: 按截止时间排序（最近的在前）
    - `createdAt`: 按创建时间排序（最新的在前，默认）

---

#### 🔍 第五步：查询单个任务（测试 Redis 缓存）

**创建新请求：**
```
方法：GET
URL：http://localhost:8080/api/tasks/1
```

**设置 Headers：**
```
Authorization: Bearer 你的token
```

**第一次点击 Send**

**IDEA 控制台输出：**
```
📝 任务已缓存: 1
```

**第二次点击 Send**

**IDEA 控制台输出：**
```
✅ 从缓存中获取任务: 1
```

✅ **Redis 缓存生效！第二次从缓存读取，速度更快**

---

#### ✏️ 第六步：更新任务

**创建新请求：**
```
方法：PUT
URL：http://localhost:8080/api/tasks/1
```

**设置 Headers：**
```
Content-Type: application/json
Authorization: Bearer 你的token
```

**设置 Body：**
```json
{
  "title": "完成项目报告（已修改）",
  "description": "需要在本周五前完成并提交",
  "status": "DOING",
  "deadline": "2026-01-15T12:00:00"
}
```

**点击 Send**

**预期响应：**
```json
{
    "code": 200,
    "message": "任务更新成功",
    "data": {
        "id": 1,
        "userId": 1,
        "title": "完成项目报告（已修改）",
        "description": "需要在本周五前完成并提交",
        "status": "DOING",
        "deadline": "2026-01-15T12:00:00",
        "createdAt": "2025-12-27T23:05:00",
        "updatedAt": "2025-12-27T23:10:00"
    }
}
```

**IDEA 控制台输出：**
```
🗑️ 缓存已删除: 1
```

✅ **任务更新成功，缓存自动删除**

---

#### 🗑️ 第七步：删除任务

**创建新请求：**
```
方法：DELETE
URL：http://localhost:8080/api/tasks/1
```

**设置 Headers：**
```
Authorization: Bearer 你的token
```

**点击 Send**

**预期响应：**
```json
{
    "code": 200,
    "message": "任务删除成功",
    "data": "任务删除成功"
}
```

**IDEA 控制台输出：**
```
🗑️ 任务和缓存已删除: 1
```

✅ **任务删除成功**

---

### 🔐 测试登录失败限制

**创建新请求：**
```
方法：POST
URL：http://localhost:8080/api/auth/login

Headers:
Content-Type: application/json

Body:
{
  "username": "testuser",
  "password": "wrongpassword"
}
```

**连续点击 Send 5次：**

- 第1次：`用户名或密码错误，还剩4次尝试机会`
- 第2次：`用户名或密码错误，还剩3次尝试机会`
- 第3次：`用户名或密码错误，还剩2次尝试机会`
- 第4次：`用户名或密码错误，还剩1次尝试机会`
- 第5次：`登录失败次数过多，账号已被锁定30分钟`

**验证锁定（改成正确密码）：**
```json
{
  "username": "testuser",
  "password": "123456"
}
```

**预期响应：**
```json
{
    "code": 500,
    "message": "登录失败次数过多，账号已被锁定XX分钟"
}
```

✅ **防暴力破解功能正常！**

**解除锁定：**

打开 Redis 命令行：
```bash
redis-cli
DEL login:fail:testuser
```

然后重新登录即可。

---

### 📊 查看操作日志

**打开 MySQL 客户端（Navicat 或命令行）：**
```sql
USE task_management;

-- 查看最近10条操作日志
SELECT 
    id,
    username,
    operation,
    execute_time,
    created_at
FROM operation_log 
ORDER BY created_at DESC 
LIMIT 10;
```

**应该看到所有操作记录：**
- 用户注册
- 用户登录
- 创建任务
- 查询任务列表
- 查询任务详情
- 更新任务
- 删除任务

---

### 💡 Postman 使用技巧

#### 1. 保存请求

每个请求测试成功后：
1. 点击 **Save** 按钮
2. 保存到 `Task Management System` Collection
3. 命名规范：`1. 用户注册`、`2. 用户登录`、`3. 创建任务` 等

#### 2. 使用环境变量

**创建环境：**
1. 点击右上角齿轮图标 ⚙️
2. 点击 **Add**
3. 环境名称：`Local`

**添加变量：**
- `baseUrl`: `http://localhost:8080`
- `token`: 留空（登录后手动填写）

**使用变量：**
```
URL: {{baseUrl}}/api/auth/register
Authorization: Bearer {{token}}
```

#### 3. 使用测试脚本自动保存 Token

**在登录请求的 Tests 标签添加：**
```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    pm.environment.set("token", jsonData.data.token);
}
```

登录成功后自动保存 token 到环境变量！

#### 4. 导出 Collection

**分享给其他人：**
1. 右键 Collection
2. Export
3. 选择 Collection v2.1
4. 保存为 `postman_collection.json`

---

## 📁 项目结构
```
taskmanagement/
├── src/
│   ├── main/
│   │   ├── java/com/example/taskmanagement/
│   │   │   ├── annotation/          # 自定义注解
│   │   │   │   └── LogOperation.java
│   │   │   ├── aspect/              # AOP切面
│   │   │   │   └── OperationLogAspect.java
│   │   │   ├── config/              # 配置类
│   │   │   │   └── RedisConfig.java
│   │   │   ├── controller/          # 控制器层
│   │   │   │   ├── UserController.java
│   │   │   │   └── TaskController.java
│   │   │   ├── dto/                 # 数据传输对象
│   │   │   │   ├── Result.java
│   │   │   │   ├── UserRegisterDTO.java
│   │   │   │   ├── UserLoginDTO.java
│   │   │   │   ├── TaskCreateDTO.java
│   │   │   │   └── TaskUpdateDTO.java
│   │   │   ├── entity/              # 实体类
│   │   │   │   ├── User.java
│   │   │   │   ├── Task.java
│   │   │   │   └── OperationLog.java
│   │   │   ├── exception/           # 异常处理
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── repository/          # 数据访问层
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── TaskRepository.java
│   │   │   │   └── OperationLogRepository.java
│   │   │   ├── service/             # 业务逻辑层
│   │   │   │   ├── UserService.java
│   │   │   │   └── TaskService.java
│   │   │   ├── util/                # 工具类
│   │   │   │   ├── JwtUtil.java
│   │   │   │   └── PasswordUtil.java
│   │   │   └── TaskmanagementApplication.java
│   │   └── resources/
│   │       ├── application.yml      # 配置文件
│   │       └── db/
│   │           └── init.sql         # 数据库初始化脚本
│   └── test/                        # 测试代码
├── pom.xml                          # Maven配置
├── README.md                        # 项目说明
└── API_EXAMPLES.md                  # API详细示例
```

---

## 🗄️ 数据库设计

### 用户表 (user)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| username | VARCHAR(50) | UNIQUE, NOT NULL | 用户名 |
| password | VARCHAR(255) | NOT NULL | 密码（MD5加密） |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 任务表 (task)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| user_id | BIGINT | NOT NULL | 用户ID |
| title | VARCHAR(200) | NOT NULL | 任务标题 |
| description | TEXT | NULL | 任务描述 |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'TODO' | 状态：TODO/DOING/DONE |
| deadline | DATETIME | NULL | 截止时间 |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 操作日志表 (operation_log)

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| user_id | BIGINT | NULL | 用户ID |
| username | VARCHAR(50) | NULL | 用户名 |
| operation | VARCHAR(100) | NULL | 操作类型 |
| method | VARCHAR(200) | NULL | 方法名 |
| params | TEXT | NULL | 请求参数 |
| result | TEXT | NULL | 返回结果 |
| ip | VARCHAR(50) | NULL | IP地址 |
| execute_time | BIGINT | NULL | 执行时间（毫秒） |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

---

## 💡 核心功能说明

### 1. JWT 认证机制

**Token 格式：**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Token 内容：**
- 用户ID
- 用户名
- 签发时间
- 过期时间（30天）

### 2. Redis 缓存策略

**缓存 Key：** `task:{taskId}`  
**过期时间：** 10分钟  
**缓存场景：** 查询单个任务详情

**流程：**
```
查询 → Redis缓存 → 命中返回 | 未命中 → 数据库 → 写入Redis → 返回
更新/删除 → 数据库 → 删除Redis缓存
```

### 3. 登录失败限制

**Redis Key：** `login:fail:{username}`  
**最多失败：** 5次  
**锁定时间：** 30分钟  
**自动解锁：** 30分钟后或成功登录

### 4. AOP 操作日志

**自动记录：**
- 所有用户操作
- 请求参数
- 返回结果
- 执行时间
- IP地址

**查询日志：**
```sql
SELECT * FROM operation_log 
ORDER BY created_at DESC;
```

---

## ❓ 常见问题

### 1. Token 无效或已过期

**解决方案：**
- 重新登录获取新 Token
- 检查 Authorization 格式：`Bearer {token}`
- 注意 `Bearer` 后面有空格

### 2. Redis 连接失败

**解决方案：**
```bash
# 检查 Redis 是否启动
redis-cli ping

# Windows 启动 Redis
cd "C:\Program Files\Redis"
redis-server.exe
```

### 3. 数据库连接失败

**解决方案：**
- 检查 MySQL 是否启动
- 确认 `application.yml` 中的用户名密码
- URL 中添加：`&allowPublicKeyRetrieval=true`

### 4. Postman 请求失败

**常见错误：**
- GET 请求忘记添加 Authorization
- POST 请求 Body 选择了 `none`
- Token 格式错误（缺少 `Bearer `或多了空格）
- URL 拼写错误

---

## 📈 后续优化建议

- [ ] 使用 BCrypt 替代 MD5 加密密码
- [ ] 添加任务分类和标签
- [ ] 实现任务提醒功能
- [ ] 添加数据导出功能
- [ ] 实现前端页面
- [ ] Docker 容器化部署
- [ ] 添加单元测试

---

## 📄 许可证

MIT License

---

**最后更新时间：** 2025-12-27
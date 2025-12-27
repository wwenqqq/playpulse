# API 使用示例

本文档提供完整的 API 使用示例，包括 Postman 和 curl 命令两种方式。

---

## 📋 目录

- [基础信息](#基础信息)
- [用户相关 API](#用户相关-api)
- [任务相关 API](#任务相关-api)
- [完整测试流程](#完整测试流程)

---

## 🔧 基础信息

**Base URL:**
```
http://localhost:8080
```

**通用响应格式:**
```json
{
  "code": 200,           // 状态码：200成功，500失败
  "message": "success",  // 提示信息
  "data": {}            // 返回数据
}
```

**认证方式:**
```
Header: Authorization: Bearer {token}
```

---

## 👤 用户相关 API

### 1. 用户注册

**接口信息:**
- **URL:** `/api/auth/register`
- **方法:** `POST`
- **需要认证:** ❌

**请求参数:**
```json
{
  "username": "testuser",    // 必填，3-50字符
  "password": "123456"       // 必填，6-20字符
}
```

**Postman 配置:**
```
POST http://localhost:8080/api/auth/register

Headers:
Content-Type: application/json

Body (raw JSON):
{
  "username": "testuser",
  "password": "123456"
}
```

**curl 命令:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123456"
  }'
```

**成功响应:**
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

**失败响应（用户名已存在）:**
```json
{
    "code": 500,
    "message": "用户名已存在",
    "data": null
}
```

---

### 2. 用户登录

**接口信息:**
- **URL:** `/api/auth/login`
- **方法:** `POST`
- **需要认证:** ❌

**请求参数:**
```json
{
  "username": "testuser",
  "password": "123456"
}
```

**Postman 配置:**
```
POST http://localhost:8080/api/auth/login

Headers:
Content-Type: application/json

Body (raw JSON):
{
  "username": "testuser",
  "password": "123456"
}
```

**curl 命令:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "123456"
  }'
```

**成功响应:**
```json
{
    "code": 200,
    "message": "登录成功",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwidXNlcm5hbWUiOiJ0ZXN0dXNlciIsImlhdCI6MTczNTMxNDAwMCwiZXhwIjoxNzM3OTA2MDAwfQ.example",
        "userId": 1,
        "username": "testuser"
    }
}
```

**⚠️ 重要：复制 `token` 的值，后续所有请求都需要！**

**失败响应（密码错误 - 第1次）:**
```json
{
    "code": 500,
    "message": "用户名或密码错误，还剩4次尝试机会",
    "data": null
}
```

**失败响应（账号锁定）:**
```json
{
    "code": 500,
    "message": "登录失败次数过多，账号已被锁定30分钟",
    "data": null
}
```

---

### 3. 获取用户信息

**接口信息:**
- **URL:** `/api/auth/info`
- **方法:** `GET`
- **需要认证:** ✅

**Postman 配置:**
```
GET http://localhost:8080/api/auth/info

Headers:
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...你的token
```

**curl 命令:**
```bash
curl -X GET http://localhost:8080/api/auth/info \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...你的token"
```

**成功响应:**
```json
{
    "code": 200,
    "message": "获取成功",
    "data": {
        "id": 1,
        "username": "testuser",
        "password": null,
        "createdAt": "2025-12-27T23:00:00",
        "updatedAt": "2025-12-27T23:00:00"
    }
}
```

---

## 📋 任务相关 API

### 1. 创建任务

**接口信息:**
- **URL:** `/api/tasks`
- **方法:** `POST`
- **需要认证:** ✅

**请求参数:**
```json
{
  "title": "完成项目报告",          // 必填，最长200字符
  "description": "需要在本周五前完成",  // 可选
  "deadline": "2025-12-31T23:59:59"  // 可选，ISO格式
}
```

**Postman 配置:**
```
POST http://localhost:8080/api/tasks

Headers:
Content-Type: application/json
Authorization: Bearer 你的token

Body (raw JSON):
{
  "title": "完成项目报告",
  "description": "需要在本周五前完成",
  "deadline": "2025-12-31T23:59:59"
}
```

**curl 命令:**
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer 你的token" \
  -d '{
    "title": "完成项目报告",
    "description": "需要在本周五前完成",
    "deadline": "2025-12-31T23:59:59"
  }'
```

**成功响应:**
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

---

### 2. 查询任务列表（分页 + 排序）

**接口信息:**
- **URL:** `/api/tasks`
- **方法:** `GET`
- **需要认证:** ✅

**查询参数:**
- `page`: 页码（从0开始），默认 0
- `size`: 每页数量，默认 10
- `sortBy`: 排序字段
    - `createdAt`: 按创建时间排序（默认）
    - `deadline`: 按截止时间排序

**示例1：按创建时间排序（默认）**

**Postman 配置:**
```
GET http://localhost:8080/api/tasks?page=0&size=10&sortBy=createdAt

Headers:
Authorization: Bearer 你的token
```

**curl 命令:**
```bash
curl -X GET "http://localhost:8080/api/tasks?page=0&size=10&sortBy=createdAt" \
  -H "Authorization: Bearer 你的token"
```

**示例2：按截止时间排序**

**Postman 配置:**
```
GET http://localhost:8080/api/tasks?page=0&size=10&sortBy=deadline

Headers:
Authorization: Bearer 你的token
```

**curl 命令:**
```bash
curl -X GET "http://localhost:8080/api/tasks?page=0&size=10&sortBy=deadline" \
  -H "Authorization: Bearer 你的token"
```

**成功响应:**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "id": 2,
        "userId": 1,
        "title": "提交作业",
        "description": "明天截止",
        "status": "TODO",
        "deadline": "2025-12-28T18:00:00",
        "createdAt": "2025-12-27T23:06:00",
        "updatedAt": "2025-12-27T23:06:00"
      },
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
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10
    },
    "totalElements": 2,
    "totalPages": 1,
    "size": 10,
    "number": 0,
    "first": true,
    "last": true,
    "empty": false
  }
}
```

---

### 3. 查询单个任务（带 Redis 缓存）

**接口信息:**
- **URL:** `/api/tasks/{id}`
- **方法:** `GET`
- **需要认证:** ✅

**Postman 配置:**
```
GET http://localhost:8080/api/tasks/1

Headers:
Authorization: Bearer 你的token
```

**curl 命令:**
```bash
curl -X GET http://localhost:8080/api/tasks/1 \
  -H "Authorization: Bearer 你的token"
```

**成功响应:**
```json
{
  "code": 200,
  "message": "success",
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

**控制台日志（首次查询）:**
```
📝 任务已缓存: 1
```

**控制台日志（第二次查询）:**
```
✅ 从缓存中获取任务: 1
```

---

### 4. 更新任务

**接口信息:**
- **URL:** `/api/tasks/{id}`
- **方法:** `PUT`
- **需要认证:** ✅

**请求参数（全部可选）:**
```json
{
  "title": "完成项目报告（已修改）",
  "description": "需要在本周五前完成并提交",
  "status": "DOING",                    // TODO/DOING/DONE
  "deadline": "2026-01-15T12:00:00"
}
```

**Postman 配置:**
```
PUT http://localhost:8080/api/tasks/1

Headers:
Content-Type: application/json
Authorization: Bearer 你的token

Body (raw JSON):
{
  "title": "完成项目报告（已修改）",
  "status": "DOING",
  "deadline": "2026-01-15T12:00:00"
}
```

**curl 命令:**
```bash
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer 你的token" \
  -d '{
    "title": "完成项目报告（已修改）",
    "status": "DOING",
    "deadline": "2026-01-15T12:00:00"
  }'
```

**成功响应:**
```json
{
  "code": 200,
  "message": "任务更新成功",
  "data": {
    "id": 1,
    "userId": 1,
    "title": "完成项目报告（已修改）",
    "description": "需要在本周五前完成",
    "status": "DOING",
    "deadline": "2026-01-15T12:00:00",
    "createdAt": "2025-12-27T23:05:00",
    "updatedAt": "2025-12-27T23:15:00"
  }
}
```

**控制台日志:**
```
🗑️ 缓存已删除: 1
```

---

### 5. 删除任务

**接口信息:**
- **URL:** `/api/tasks/{id}`
- **方法:** `DELETE`
- **需要认证:** ✅

**Postman 配置:**
```
DELETE http://localhost:8080/api/tasks/1

Headers:
Authorization: Bearer 你的token
```

**curl 命令:**
```bash
curl -X DELETE http://localhost:8080/api/tasks/1 \
  -H "Authorization: Bearer 你的token"
```

**成功响应:**
```json
{
  "code": 200,
  "message": "任务删除成功",
  "data": "任务删除成功"
}
```

**控制台日志:**
```
🗑️ 任务和缓存已删除: 1
```

---

## 🔄 完整测试流程

### 场景：完整的任务管理流程
```bash
# 1. 注册用户
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'

# 2. 登录获取 Token
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}' \
  | jq -r '.data.token')

echo "Token: $TOKEN"

# 3. 创建任务1
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title":"完成项目报告",
    "description":"周五截止",
    "deadline":"2025-12-31T23:59:59"
  }'

# 4. 创建任务2
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title":"提交作业",
    "description":"明天截止",
    "deadline":"2025-12-28T18:00:00"
  }'

# 5. 查询任务列表（按截止时间排序）
curl -X GET "http://localhost:8080/api/tasks?sortBy=deadline" \
  -H "Authorization: Bearer $TOKEN"

# 6. 查询任务详情（首次，从数据库）
curl -X GET http://localhost:8080/api/tasks/1 \
  -H "Authorization: Bearer $TOKEN"

# 7. 再次查询（第二次，从 Redis 缓存）
curl -X GET http://localhost:8080/api/tasks/1 \
  -H "Authorization: Bearer $TOKEN"

# 8. 更新任务状态
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"status":"DOING"}'

# 9. 删除任务
curl -X DELETE http://localhost:8080/api/tasks/2 \
  -H "Authorization: Bearer $TOKEN"

# 10. 查询最终任务列表
curl -X GET http://localhost:8080/api/tasks \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📊 测试登录失败限制
```bash
# 连续5次输入错误密码
for i in {1..5}; do
  echo "第 $i 次尝试："
  curl -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username":"testuser","password":"wrongpassword"}'
  echo ""
done

# 第5次后账号被锁定
# 输出：登录失败次数过多，账号已被锁定30分钟

# 即使输入正确密码也无法登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'
```

---

## 💡 常见问题

### Q1: Token 无效或已过期

**错误信息:**
```json
{
  "code": 500,
  "message": "token无效或已过期"
}
```

**解决方案:**
- 重新登录获取新 Token
- 检查 Authorization 格式：`Bearer {token}`
- 确保 `Bearer` 后面有空格

---

### Q2: 未登录或 token 无效

**错误信息:**
```json
{
  "code": 500,
  "message": "未登录或token无效"
}
```

**解决方案:**
- 检查是否添加了 `Authorization` 请求头
- 检查 Token 前面是否有 `Bearer `

---

### Q3: 参数校验失败

**错误信息:**
```json
{
  "code": 400,
  "message": "任务标题不能为空"
}
```

**解决方案:**
- 检查必填字段是否提供
- 检查字段长度是否符合要求

---

## 📝 备注

- 所有时间格式使用 ISO 8601：`YYYY-MM-DDTHH:mm:ss`
- Token 有效期 30 天
- 分页页码从 0 开始
- Redis 缓存过期时间 10 分钟
- 登录失败锁定时间 30 分钟
package com.example.taskmanagement.controller;

import com.example.taskmanagement.annotation.LogOperation;
import com.example.taskmanagement.dto.Result;
import com.example.taskmanagement.dto.UserLoginDTO;
import com.example.taskmanagement.dto.UserRegisterDTO;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.service.UserService;
import com.example.taskmanagement.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@ConditionalOnExpression("'${app.role:all}' == 'auth' or '${app.role:all}' == 'all'")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @LogOperation("用户注册")
    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody UserRegisterDTO dto) {
        User user = userService.register(dto);
        user.setPassword(null);
        return Result.success("注册成功", user);
    }

    @LogOperation("用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody UserLoginDTO dto) {
        Map<String, Object> result = userService.login(dto);
        return Result.success("登录成功", result);
    }

    @LogOperation("获取用户信息")
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestHeader("Authorization") String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Result.error("未登录或token无效");
        }

        String token = authorization.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return Result.error("token无效或已过期");
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userService.getUserInfo(userId);
        user.setPassword(null);
        return Result.success("获取成功", user);
    }
}

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
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@ConditionalOnExpression("'${app.role:all}' == 'auth' or '${app.role:all}' == 'all'")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @LogOperation("Register user")
    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody UserRegisterDTO dto) {
        User user = userService.register(dto);
        user.setPassword(null);
        return Result.success("Account created", user);
    }

    @LogOperation("Sign in user")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody UserLoginDTO dto) {
        return Result.success("Signed in", userService.login(dto));
    }

    @LogOperation("Get current user")
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestHeader("Authorization") String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Result.error("Authentication required");
        }

        String token = authorization.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return Result.error("Your session is invalid or has expired");
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userService.getUserInfo(userId);
        user.setPassword(null);
        return Result.success("User loaded", user);
    }
}

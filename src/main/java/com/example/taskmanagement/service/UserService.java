package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.UserLoginDTO;
import com.example.taskmanagement.dto.UserRegisterDTO;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.repository.UserRepository;
import com.example.taskmanagement.util.JwtUtil;
import com.example.taskmanagement.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LOGIN_FAIL_PREFIX = "login:fail:";
    private static final int MAX_FAIL_COUNT = 5; // 最大失败次数
    private static final long LOCK_TIME = 30; // 锁定时间（分钟）

    // 用户注册
    @Transactional
    public User register(UserRegisterDTO dto) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(PasswordUtil.encrypt(dto.getPassword()));

        return userRepository.save(user);
    }

    // 用户登录（带失败次数限制）
    public Map<String, Object> login(UserLoginDTO dto) {
        String username = dto.getUsername();
        String failKey = LOGIN_FAIL_PREFIX + username;

        // 检查是否被锁定
        Integer failCount = (Integer) redisTemplate.opsForValue().get(failKey);
        if (failCount != null && failCount >= MAX_FAIL_COUNT) {
            Long ttl = redisTemplate.getExpire(failKey, TimeUnit.MINUTES);
            throw new RuntimeException("登录失败次数过多，账号已被锁定" + ttl + "分钟");
        }

        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    // 用户不存在也记录失败次数
                    recordLoginFailure(failKey);
                    return new RuntimeException("用户名或密码错误");
                });

        // 验证密码
        if (!PasswordUtil.verify(dto.getPassword(), user.getPassword())) {
            // 记录登录失败
            recordLoginFailure(failKey);
            int remaining = MAX_FAIL_COUNT - (failCount == null ? 1 : failCount + 1);
            if (remaining > 0) {
                throw new RuntimeException("用户名或密码错误，还剩" + remaining + "次尝试机会");
            } else {
                throw new RuntimeException("登录失败次数过多，账号已被锁定" + LOCK_TIME + "分钟");
            }
        }

        // 登录成功，清除失败记录
        redisTemplate.delete(failKey);
        System.out.println("✅ 用户 " + username + " 登录成功，清除失败记录");

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 返回用户信息和Token
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());

        return result;
    }

    // 记录登录失败
    private void recordLoginFailure(String failKey) {
        Integer failCount = (Integer) redisTemplate.opsForValue().get(failKey);
        if (failCount == null) {
            failCount = 0;
        }
        failCount++;

        // 设置失败次数，30分钟过期
        redisTemplate.opsForValue().set(failKey, failCount, LOCK_TIME, TimeUnit.MINUTES);
        System.out.println("❌ 登录失败，当前失败次数：" + failCount);
    }

    // 获取用户信息
    public User getUserInfo(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
}
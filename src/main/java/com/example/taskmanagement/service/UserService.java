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

    private static final String LOGIN_FAIL_PREFIX = "login:fail:";
    private static final int MAX_FAIL_COUNT = 5;
    private static final long LOCK_TIME_MINUTES = 30;

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public User register(UserRegisterDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("That username is already in use");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(PasswordUtil.encrypt(dto.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public Map<String, Object> login(UserLoginDTO dto) {
        String username = dto.getUsername();
        String failKey = LOGIN_FAIL_PREFIX + username;
        Integer failCount = (Integer) redisTemplate.opsForValue().get(failKey);

        if (failCount != null && failCount >= MAX_FAIL_COUNT) {
            Long ttl = redisTemplate.getExpire(failKey, TimeUnit.MINUTES);
            long minutes = ttl == null || ttl < 1 ? LOCK_TIME_MINUTES : ttl;
            throw new RuntimeException("Too many sign-in attempts. Try again in " + minutes + " minutes");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    recordLoginFailure(failKey);
                    return new RuntimeException("Incorrect username or password");
                });

        if (!PasswordUtil.verify(dto.getPassword(), user.getPassword())) {
            int updatedFailCount = recordLoginFailure(failKey);
            int remaining = MAX_FAIL_COUNT - updatedFailCount;
            if (remaining > 0) {
                throw new RuntimeException("Incorrect username or password. " + remaining + " attempts remaining");
            }
            throw new RuntimeException("Too many sign-in attempts. Try again in " + LOCK_TIME_MINUTES + " minutes");
        }

        if (PasswordUtil.needsUpgrade(user.getPassword())) {
            user.setPassword(PasswordUtil.encrypt(dto.getPassword()));
            userRepository.save(user);
        }

        redisTemplate.delete(failKey);
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        return result;
    }

    private int recordLoginFailure(String failKey) {
        Integer failCount = (Integer) redisTemplate.opsForValue().get(failKey);
        int updatedFailCount = (failCount == null ? 0 : failCount) + 1;
        redisTemplate.opsForValue().set(failKey, updatedFailCount, LOCK_TIME_MINUTES, TimeUnit.MINUTES);
        return updatedFailCount;
    }

    public User getUserInfo(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

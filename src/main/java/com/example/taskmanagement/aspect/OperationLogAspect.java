package com.example.taskmanagement.aspect;

import com.example.taskmanagement.annotation.LogOperation;
import com.example.taskmanagement.entity.OperationLog;
import com.example.taskmanagement.repository.OperationLogRepository;
import com.example.taskmanagement.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogRepository operationLogRepository;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Around("@annotation(com.example.taskmanagement.annotation.LogOperation)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        // 创建日志对象
        OperationLog log = new OperationLog();

        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogOperation logOperation = method.getAnnotation(LogOperation.class);
        if (logOperation != null) {
            log.setOperation(logOperation.value());
        }

        // 获取用户信息
        if (request != null) {
            String authorization = request.getHeader("Authorization");
            if (authorization != null && authorization.startsWith("Bearer ")) {
                try {
                    String token = authorization.substring(7);
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    String username = jwtUtil.getUsernameFromToken(token);
                    log.setUserId(userId);
                    log.setUsername(username);
                } catch (Exception e) {
                    // Token解析失败，不记录用户信息
                }
            }

            // 获取IP
            log.setIp(getIpAddress(request));
        }

        // 方法信息
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        log.setMethod(className + "." + methodName);

        // 请求参数
        Object[] args = joinPoint.getArgs();
        try {
            String params = objectMapper.writeValueAsString(args);
            if (params.length() > 5000) {
                params = params.substring(0, 5000) + "...";
            }
            log.setParams(params);
        } catch (Exception e) {
            log.setParams("参数序列化失败");
        }

        // 执行方法
        Object result = null;
        try {
            result = joinPoint.proceed();

            // 记录返回结果
            try {
                String resultStr = objectMapper.writeValueAsString(result);
                if (resultStr.length() > 5000) {
                    resultStr = resultStr.substring(0, 5000) + "...";
                }
                log.setResult(resultStr);
            } catch (Exception e) {
                log.setResult("结果序列化失败");
            }

            return result;
        } catch (Throwable e) {
            log.setResult("异常：" + e.getMessage());
            throw e;
        } finally {
            // 执行时间
            long executeTime = System.currentTimeMillis() - startTime;
            log.setExecuteTime(executeTime);

            // 保存日志
            try {
                operationLogRepository.save(log);
                System.out.println("📝 操作日志已记录：" + log.getOperation() + " - " + executeTime + "ms");
            } catch (Exception e) {
                System.err.println("❌ 操作日志保存失败：" + e.getMessage());
            }
        }
    }

    // 获取IP地址
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
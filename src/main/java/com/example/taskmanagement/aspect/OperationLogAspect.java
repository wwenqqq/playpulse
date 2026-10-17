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

    private static final int MAX_LOG_LENGTH = 5_000;

    private final OperationLogRepository operationLogRepository;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Around("@annotation(com.example.taskmanagement.annotation.LogOperation)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        boolean authenticationAction = "register".equals(signature.getName()) || "login".equals(signature.getName());

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        OperationLog operationLog = new OperationLog();

        LogOperation annotation = method.getAnnotation(LogOperation.class);
        if (annotation != null) operationLog.setOperation(annotation.value());

        if (request != null) {
            setCurrentUser(operationLog, request.getHeader("Authorization"));
            operationLog.setIp(getIpAddress(request));
        }

        operationLog.setMethod(joinPoint.getTarget().getClass().getName() + "." + signature.getName());
        operationLog.setParams(authenticationAction
                ? "[authentication payload redacted]"
                : serialize(joinPoint.getArgs(), "Request serialization failed"));

        try {
            Object result = joinPoint.proceed();
            operationLog.setResult(authenticationAction
                    ? "[authentication result redacted]"
                    : serialize(result, "Result serialization failed"));
            return result;
        } catch (Throwable throwable) {
            operationLog.setResult("Exception: " + throwable.getClass().getSimpleName());
            throw throwable;
        } finally {
            operationLog.setExecuteTime(System.currentTimeMillis() - startTime);
            try {
                operationLogRepository.save(operationLog);
            } catch (Exception exception) {
                System.err.println("Operation log could not be saved: " + exception.getMessage());
            }
        }
    }

    private void setCurrentUser(OperationLog operationLog, String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) return;
        try {
            String token = authorization.substring(7);
            operationLog.setUserId(jwtUtil.getUserIdFromToken(token));
            operationLog.setUsername(jwtUtil.getUsernameFromToken(token));
        } catch (Exception ignored) {
            // Invalid tokens are handled by the controller; logging remains anonymous.
        }
    }

    private String serialize(Object value, String fallback) {
        try {
            String serialized = objectMapper.writeValueAsString(value);
            return serialized.length() > MAX_LOG_LENGTH
                    ? serialized.substring(0, MAX_LOG_LENGTH) + "..."
                    : serialized;
        } catch (Exception exception) {
            return fallback;
        }
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) ip = request.getRemoteAddr();
        return ip;
    }
}

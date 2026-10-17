package com.example.taskmanagement.exception;

import com.example.taskmanagement.dto.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException exception) {
        return Result.error(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException exception) {
        return Result.error(400, validationMessages(exception.getBindingResult().getFieldErrors().stream()));
    }

    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException exception) {
        return Result.error(400, validationMessages(exception.getBindingResult().getFieldErrors().stream()));
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception exception) {
        exception.printStackTrace();
        return Result.error("Internal server error");
    }

    private String validationMessages(java.util.stream.Stream<FieldError> errors) {
        return errors.map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
    }
}

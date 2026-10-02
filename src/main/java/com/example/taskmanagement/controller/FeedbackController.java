package com.example.taskmanagement.controller;
import com.example.taskmanagement.dto.*;
import com.example.taskmanagement.entity.Feedback;
import com.example.taskmanagement.service.FeedbackService;
import com.example.taskmanagement.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/feedback") @RequiredArgsConstructor public class FeedbackController {private final FeedbackService service;private final JwtUtil jwt;private Long uid(String h){if(h==null||!h.startsWith("Bearer "))throw new RuntimeException("Authentication required");String t=h.substring(7);if(!jwt.validateToken(t))throw new RuntimeException("Invalid token");return jwt.getUserIdFromToken(t);} @PostMapping public Result<FeedbackView> create(@RequestHeader("Authorization")String h,@Valid @RequestBody FeedbackCreateDTO d){return Result.success("Feedback submitted",service.create(uid(h),d));} @GetMapping public Result<Page<FeedbackView>> list(@RequestParam(required=false)Feedback.Category category,@RequestParam(required=false)Feedback.Status status,@RequestParam(required=false)String search,@RequestParam(defaultValue="0")int page,@RequestParam(defaultValue="20")int size,@RequestParam(defaultValue="latest")String sort){return Result.success(service.list(category,status,search,page,size,sort));}@GetMapping("/dashboard")public Result<Map<String,Object>> dashboard(){return Result.success(service.dashboard());}@GetMapping("/{id}")public Result<FeedbackView> get(@PathVariable Long id){return Result.success(service.get(id));}@PutMapping("/{id}")public Result<FeedbackView> update(@RequestHeader("Authorization")String h,@PathVariable Long id,@RequestBody FeedbackUpdateDTO d){return Result.success("Updated",service.update(uid(h),id,d));}@DeleteMapping("/{id}")public Result<String> delete(@RequestHeader("Authorization")String h,@PathVariable Long id){service.delete(uid(h),id);return Result.success("Deleted");}}

package com.example.taskmanagement.controller;
import com.example.taskmanagement.dto.*;
import com.example.taskmanagement.entity.FeedbackComment;
import com.example.taskmanagement.service.InteractionService;
import com.example.taskmanagement.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/feedback/{feedbackId}") @RequiredArgsConstructor public class InteractionController {private final InteractionService service;private final JwtUtil jwt;private Long uid(String h){if(h==null||!h.startsWith("Bearer "))throw new RuntimeException("Authentication required");String t=h.substring(7);if(!jwt.validateToken(t))throw new RuntimeException("Invalid token");return jwt.getUserIdFromToken(t);}@PostMapping("/votes")public Result<Map<String,Boolean>> vote(@RequestHeader("Authorization")String h,@PathVariable Long feedbackId){return Result.success(Map.of("voted",service.toggle(uid(h),feedbackId)));}@GetMapping("/comments")public Result<List<FeedbackComment>> comments(@PathVariable Long feedbackId){return Result.success(service.comments(feedbackId));}@PostMapping("/comments")public Result<FeedbackComment> comment(@RequestHeader("Authorization")String h,@PathVariable Long feedbackId,@Valid @RequestBody CommentCreateDTO d){return Result.success("Comment posted",service.comment(uid(h),feedbackId,d));}}

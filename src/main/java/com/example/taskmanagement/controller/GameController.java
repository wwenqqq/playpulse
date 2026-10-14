package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.Result;
import com.example.taskmanagement.dto.GameCreateDTO;
import com.example.taskmanagement.entity.Game;
import com.example.taskmanagement.entity.LibraryEntry;
import com.example.taskmanagement.service.GameCatalogService;
import com.example.taskmanagement.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.*;

@RestController @RequestMapping("/api/games") @RequiredArgsConstructor @ConditionalOnExpression("'${app.role:all}' == 'feedback' or '${app.role:all}' == 'all'")
public class GameController {
    private final GameCatalogService service; private final JwtUtil jwt;
    private Long uid(String header) { if (header == null || !header.startsWith("Bearer ")) throw new RuntimeException("Authentication required"); String token = header.substring(7); if (!jwt.validateToken(token)) throw new RuntimeException("Invalid token"); return jwt.getUserIdFromToken(token); }
    @GetMapping public Result<List<Game>> list(@RequestParam(required = false) String search) { return Result.success(service.list(search)); }
    @PostMapping public Result<Game> create(@RequestHeader("Authorization") String header, @Valid @RequestBody GameCreateDTO data) { return Result.success("Game added", service.create(uid(header), data)); }
    @GetMapping("/{id}") public Result<Game> get(@PathVariable Long id) { return Result.success(service.get(id)); }
    @GetMapping("/library/mine") public Result<List<Map<String,Object>>> mine(@RequestHeader("Authorization") String header) { return Result.success(service.library(uid(header))); }
    @PostMapping("/{id}/library") public Result<LibraryEntry> save(@RequestHeader("Authorization") String header, @PathVariable Long id, @RequestBody(required = false) Map<String,String> body) {
        String raw = body == null ? null : body.get("status"); LibraryEntry.Status status = raw == null ? LibraryEntry.Status.WISHLIST : LibraryEntry.Status.valueOf(raw);
        return Result.success("Library updated", service.saveLibrary(uid(header), id, status));
    }
}

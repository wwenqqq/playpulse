package com.example.taskmanagement.repository;

import com.example.taskmanagement.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByTitleContainingIgnoreCaseOrStudioContainingIgnoreCase(String title, String studio);
    boolean existsByTitle(String title);
}

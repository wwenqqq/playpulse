package com.example.taskmanagement.repository;

import com.example.taskmanagement.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByTitleContainingIgnoreCaseOrStudioContainingIgnoreCase(String title, String studio);
    Optional<Game> findByTitle(String title);
    boolean existsByTitle(String title);
}

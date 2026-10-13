package com.example.taskmanagement.repository;

import com.example.taskmanagement.entity.LibraryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LibraryEntryRepository extends JpaRepository<LibraryEntry, Long> {
    List<LibraryEntry> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<LibraryEntry> findByUserIdAndGameId(Long userId, Long gameId);
}

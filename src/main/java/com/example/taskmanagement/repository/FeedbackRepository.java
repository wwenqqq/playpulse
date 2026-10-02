package com.example.taskmanagement.repository;
import com.example.taskmanagement.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface FeedbackRepository extends JpaRepository<Feedback,Long> { Page<Feedback> findByCategory(Feedback.Category category, Pageable pageable); Page<Feedback> findByStatus(Feedback.Status status, Pageable pageable); Page<Feedback> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title,String description,Pageable pageable); Optional<Feedback> findByIdAndUserId(Long id,Long userId); long countByStatus(Feedback.Status status); }

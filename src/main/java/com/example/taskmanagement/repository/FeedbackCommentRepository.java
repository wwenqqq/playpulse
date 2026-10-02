package com.example.taskmanagement.repository;
import com.example.taskmanagement.entity.FeedbackComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface FeedbackCommentRepository extends JpaRepository<FeedbackComment,Long> { List<FeedbackComment> findByFeedbackIdOrderByCreatedAtAsc(Long feedbackId); long countByFeedbackId(Long feedbackId); }

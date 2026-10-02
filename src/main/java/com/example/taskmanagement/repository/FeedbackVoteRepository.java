package com.example.taskmanagement.repository;
import com.example.taskmanagement.entity.FeedbackVote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface FeedbackVoteRepository extends JpaRepository<FeedbackVote,Long> { long countByFeedbackId(Long feedbackId); Optional<FeedbackVote> findByFeedbackIdAndUserId(Long feedbackId,Long userId); }

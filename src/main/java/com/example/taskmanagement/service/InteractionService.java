package com.example.taskmanagement.service;
import com.example.taskmanagement.dto.CommentCreateDTO;
import com.example.taskmanagement.entity.*;
import com.example.taskmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service @RequiredArgsConstructor public class InteractionService { private final FeedbackRepository feedbacks;private final FeedbackVoteRepository votes;private final FeedbackCommentRepository comments; @Transactional public boolean toggle(Long uid,Long fid){feedbacks.findById(fid).orElseThrow(()->new RuntimeException("Feedback not found"));return votes.findByFeedbackIdAndUserId(fid,uid).map(v->{votes.delete(v);return false;}).orElseGet(()->{FeedbackVote v=new FeedbackVote();v.setFeedbackId(fid);v.setUserId(uid);votes.save(v);return true;});} @Transactional public FeedbackComment comment(Long uid,Long fid,CommentCreateDTO d){feedbacks.findById(fid).orElseThrow(()->new RuntimeException("Feedback not found"));FeedbackComment c=new FeedbackComment();c.setFeedbackId(fid);c.setUserId(uid);c.setContent(d.getContent());return comments.save(c);} public List<FeedbackComment> comments(Long fid){return comments.findByFeedbackIdOrderByCreatedAtAsc(fid);} }

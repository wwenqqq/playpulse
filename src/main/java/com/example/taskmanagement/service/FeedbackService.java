package com.example.taskmanagement.service;
import com.example.taskmanagement.dto.*;
import com.example.taskmanagement.entity.Feedback;
import com.example.taskmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
@Service @RequiredArgsConstructor public class FeedbackService {
 private final FeedbackRepository feedbacks; private final FeedbackVoteRepository votes; private final FeedbackCommentRepository comments;
 @Transactional public FeedbackView create(Long uid,FeedbackCreateDTO d){Feedback f=new Feedback();f.setUserId(uid);f.setTitle(d.getTitle());f.setDescription(d.getDescription());f.setCategory(d.getCategory());f.setPriority(d.getPriority()==null?Feedback.Priority.MEDIUM:d.getPriority());f.setGameVersion(d.getGameVersion());return view(feedbacks.save(f));}
 public Page<FeedbackView> list(Feedback.Category c,Feedback.Status s,String q,int p,int z,String sort){Pageable page=PageRequest.of(p,Math.min(z,50),Sort.by(Sort.Direction.DESC,"createdAt"));Page<Feedback> r=q!=null&&!q.isBlank()?feedbacks.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(q,q,page):c!=null?feedbacks.findByCategory(c,page):s!=null?feedbacks.findByStatus(s,page):feedbacks.findAll(page);var content=r.map(this::view).getContent();if("popular".equals(sort))content=content.stream().sorted((a,b)->Long.compare(b.getVoteCount(),a.getVoteCount())).toList();return new PageImpl<>(content,page,r.getTotalElements());}
 public Page<FeedbackView> mine(Long uid,int p,int z){Pageable page=PageRequest.of(p,Math.min(z,50));return feedbacks.findByUserIdOrderByCreatedAtDesc(uid,page).map(this::view);}
 public FeedbackView get(Long id){return view(feedbacks.findById(id).orElseThrow(()->new RuntimeException("Feedback not found")));}
 @Transactional public FeedbackView update(Long uid,Long id,FeedbackUpdateDTO d){Feedback f=feedbacks.findByIdAndUserId(id,uid).orElseThrow(()->new RuntimeException("You can update only your own feedback"));if(d.getTitle()!=null)f.setTitle(d.getTitle());if(d.getDescription()!=null)f.setDescription(d.getDescription());if(d.getCategory()!=null)f.setCategory(d.getCategory());if(d.getPriority()!=null)f.setPriority(d.getPriority());if(d.getStatus()!=null)f.setStatus(d.getStatus());if(d.getGameVersion()!=null)f.setGameVersion(d.getGameVersion());return view(feedbacks.save(f));}
 @Transactional public void delete(Long uid,Long id){feedbacks.delete(feedbacks.findByIdAndUserId(id,uid).orElseThrow(()->new RuntimeException("You can delete only your own feedback")));}
 public Map<String,Object> dashboard(){return Map.of("total",feedbacks.count(),"reported",feedbacks.countByStatus(Feedback.Status.REPORTED),"triaged",feedbacks.countByStatus(Feedback.Status.TRIAGED),"inProgress",feedbacks.countByStatus(Feedback.Status.IN_PROGRESS),"fixed",feedbacks.countByStatus(Feedback.Status.FIXED));}
 private FeedbackView view(Feedback f){FeedbackView v=new FeedbackView();v.setId(f.getId());v.setUserId(f.getUserId());v.setTitle(f.getTitle());v.setDescription(f.getDescription());v.setCategory(f.getCategory());v.setStatus(f.getStatus());v.setPriority(f.getPriority());v.setGameVersion(f.getGameVersion());v.setCreatedAt(f.getCreatedAt());v.setUpdatedAt(f.getUpdatedAt());v.setVoteCount(votes.countByFeedbackId(f.getId()));v.setCommentCount(comments.countByFeedbackId(f.getId()));return v;}
}

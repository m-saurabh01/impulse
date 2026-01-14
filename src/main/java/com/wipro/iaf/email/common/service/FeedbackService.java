package com.wipro.iaf.email.common.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wipro.iaf.email.common.entity.Feedback;
import com.wipro.iaf.email.common.entity.Feedback.FeedbackType;
import com.wipro.iaf.email.common.repo.FeedbackRepository;
import com.wipro.iaf.email.user.entity.User;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepo;

    public FeedbackService(FeedbackRepository feedbackRepo) {
        this.feedbackRepo = feedbackRepo;
    }

    @Transactional
    public Feedback submitFeedback(User user, String name, String email, 
                                   FeedbackType type, String subject, String message) {
        Feedback feedback = new Feedback(user, name, email, type, subject, message);
        return feedbackRepo.save(feedback);
    }

    @Transactional(readOnly = true)
    public Page<Feedback> getAllFeedback(Pageable pageable) {
        return feedbackRepo.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Feedback> getFeedbackByType(FeedbackType type, Pageable pageable) {
        return feedbackRepo.findByTypeOrderByCreatedAtDesc(type, pageable);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount() {
        return feedbackRepo.countUnread();
    }

    @Transactional
    public void markAsRead(Long feedbackId) {
        feedbackRepo.findById(feedbackId).ifPresent(f -> {
            f.setRead(true);
            feedbackRepo.save(f);
        });
    }

    @Transactional
    public void deleteFeedback(Long feedbackId) {
        feedbackRepo.deleteById(feedbackId);
    }
}

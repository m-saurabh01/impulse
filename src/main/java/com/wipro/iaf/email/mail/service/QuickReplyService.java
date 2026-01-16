package com.wipro.iaf.email.mail.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wipro.iaf.email.mail.entity.QuickReply;
import com.wipro.iaf.email.mail.repo.QuickReplyRepository;
import com.wipro.iaf.email.user.repo.UserRepository;

@Service
public class QuickReplyService {

    private static final int MAX_QUICK_REPLIES = 10;

    private final QuickReplyRepository quickReplyRepo;
    private final UserRepository userRepo;

    public QuickReplyService(QuickReplyRepository quickReplyRepo, UserRepository userRepo) {
        this.quickReplyRepo = quickReplyRepo;
        this.userRepo = userRepo;
    }

    /**
     * Get all quick replies for a user
     */
    @Transactional(readOnly = true)
    public List<QuickReply> getQuickReplies(Long userId) {
        return quickReplyRepo.findByUserIdOrderBySortOrderAsc(userId);
    }

    /**
     * Create a new quick reply
     */
    @Transactional
    public QuickReply createQuickReply(Long userId, String title, String content) {
        // Check limit
        if (quickReplyRepo.countByUserId(userId) >= MAX_QUICK_REPLIES) {
            throw new IllegalStateException("Maximum number of quick replies reached (" + MAX_QUICK_REPLIES + ")");
        }

        QuickReply qr = new QuickReply();
        qr.setUser(userRepo.getById(userId));
        qr.setTitle(title);
        qr.setContent(content);
        qr.setSortOrder((int) quickReplyRepo.countByUserId(userId));

        return quickReplyRepo.save(qr);
    }

    /**
     * Update an existing quick reply
     */
    @Transactional
    public QuickReply updateQuickReply(Long userId, Long quickReplyId, String title, String content) {
        QuickReply qr = quickReplyRepo.findByIdAndUserId(quickReplyId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Quick reply not found"));

        qr.setTitle(title);
        qr.setContent(content);
        qr.setUpdatedAt(LocalDateTime.now());

        return quickReplyRepo.save(qr);
    }

    /**
     * Delete a quick reply
     */
    @Transactional
    public void deleteQuickReply(Long userId, Long quickReplyId) {
        quickReplyRepo.findByIdAndUserId(quickReplyId, userId)
                .ifPresent(quickReplyRepo::delete);
    }

    /**
     * Get a specific quick reply
     */
    @Transactional(readOnly = true)
    public QuickReply getQuickReply(Long userId, Long quickReplyId) {
        return quickReplyRepo.findByIdAndUserId(quickReplyId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Quick reply not found"));
    }
}

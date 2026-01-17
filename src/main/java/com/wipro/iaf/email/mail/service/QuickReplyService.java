package com.wipro.iaf.email.mail.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wipro.iaf.email.mail.entity.QuickReply;
import com.wipro.iaf.email.mail.repo.QuickReplyRepository;
import com.wipro.iaf.email.user.repo.UserRepository;

/**
 * Service class for managing quick reply templates in the PulseMail application.
 * <p>
 * Quick replies are pre-defined response templates that users can create and use
 * to quickly respond to common emails. This service provides:
 * <ul>
 *   <li>CRUD operations for quick reply templates</li>
 *   <li>User-specific template management with sort ordering</li>
 *   <li>Enforcement of maximum template limits per user</li>
 * </ul>
 * </p>
 * <p>
 * Each user can have a maximum of {@value #MAX_QUICK_REPLIES} quick replies.
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-16
 * @see QuickReply
 */
@Service
public class QuickReplyService {

    /** Maximum number of quick replies allowed per user */
    private static final int MAX_QUICK_REPLIES = 10;

    private final QuickReplyRepository quickReplyRepo;
    private final UserRepository userRepo;

    /**
     * Constructs a new QuickReplyService with required dependencies.
     * 
     * @param quickReplyRepo repository for quick reply CRUD operations
     * @param userRepo repository for user lookups
     */
    public QuickReplyService(QuickReplyRepository quickReplyRepo, UserRepository userRepo) {
        this.quickReplyRepo = quickReplyRepo;
        this.userRepo = userRepo;
    }

    /**
     * Retrieves all quick replies for a user, ordered by sort order.
     * 
     * @param userId the ID of the user whose quick replies to retrieve
     * @return list of QuickReply entities sorted by sort order ascending
     */
    @Transactional(readOnly = true)
    public List<QuickReply> getQuickReplies(Long userId) {
        return quickReplyRepo.findByUserIdOrderBySortOrderAsc(userId);
    }

    /**
     * Creates a new quick reply template for a user.
     * <p>
     * The sort order is automatically set based on the current count of user's quick replies.
     * </p>
     * 
     * @param userId the ID of the user creating the quick reply
     * @param title the title/name of the quick reply template
     * @param content the content/body of the quick reply template
     * @return the newly created QuickReply entity
     * @throws IllegalStateException if user has reached maximum allowed quick replies
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
     * Updates an existing quick reply template.
     * <p>
     * Updates the title, content, and sets the updated timestamp to now.
     * </p>
     * 
     * @param userId the ID of the user who owns the quick reply
     * @param quickReplyId the ID of the quick reply to update
     * @param title the new title for the quick reply
     * @param content the new content for the quick reply
     * @return the updated QuickReply entity
     * @throws IllegalArgumentException if quick reply not found or doesn't belong to user
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
     * Deletes a quick reply template.
     * <p>
     * If the quick reply doesn't exist or doesn't belong to the user, no action is taken.
     * </p>
     * 
     * @param userId the ID of the user who owns the quick reply
     * @param quickReplyId the ID of the quick reply to delete
     */
    @Transactional
    public void deleteQuickReply(Long userId, Long quickReplyId) {
        quickReplyRepo.findByIdAndUserId(quickReplyId, userId)
                .ifPresent(quickReplyRepo::delete);
    }

    /**
     * Retrieves a specific quick reply by ID for a user.
     * 
     * @param userId the ID of the user who owns the quick reply
     * @param quickReplyId the ID of the quick reply to retrieve
     * @return the QuickReply entity
     * @throws IllegalArgumentException if quick reply not found or doesn't belong to user
     */
    @Transactional(readOnly = true)
    public QuickReply getQuickReply(Long userId, Long quickReplyId) {
        return quickReplyRepo.findByIdAndUserId(quickReplyId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Quick reply not found"));
    }
}

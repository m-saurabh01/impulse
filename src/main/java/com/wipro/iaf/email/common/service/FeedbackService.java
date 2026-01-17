package com.wipro.iaf.email.common.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wipro.iaf.email.common.entity.Feedback;
import com.wipro.iaf.email.common.entity.Feedback.FeedbackType;
import com.wipro.iaf.email.common.repo.FeedbackRepository;
import com.wipro.iaf.email.user.entity.User;

/**
 * Service layer for managing user feedback operations.
 * 
 * <p>Provides business logic for submitting, retrieving, and managing feedback entries.
 * All database operations are wrapped in appropriate transactions for data consistency.</p>
 * 
 * <p>Key responsibilities:
 * <ul>
 *   <li>Submitting new feedback from users</li>
 *   <li>Retrieving feedback with pagination and filtering</li>
 *   <li>Managing read status of feedback entries</li>
 *   <li>Providing unread counts for notification badges</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see Feedback
 * @see FeedbackRepository
 */
@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepo;

    /**
     * Constructs the FeedbackService with required dependencies.
     * 
     * @param feedbackRepo the repository for feedback database operations
     */
    public FeedbackService(FeedbackRepository feedbackRepo) {
        this.feedbackRepo = feedbackRepo;
    }

    /**
     * Submits a new feedback entry to the system.
     * 
     * <p>Creates and persists a new feedback record with the provided details.
     * The creation timestamp is automatically set by the entity lifecycle callback.</p>
     * 
     * @param user    the user submitting feedback (can be null for anonymous submissions)
     * @param name    the name of the person submitting feedback
     * @param email   the email address for follow-up communication
     * @param type    the category of feedback (FEEDBACK, SUGGESTION, BUG_REPORT, FEATURE_REQUEST)
     * @param subject a brief subject line describing the feedback
     * @param message the detailed message content
     * @return the persisted {@link Feedback} entity with generated ID
     */
    @Transactional
    public Feedback submitFeedback(User user, String name, String email, 
                                   FeedbackType type, String subject, String message) {
        Feedback feedback = new Feedback(user, name, email, type, subject, message);
        return feedbackRepo.save(feedback);
    }

    /**
     * Retrieves all feedback entries with pagination.
     * 
     * <p>Returns feedback ordered by creation date (newest first) for administrative review.</p>
     * 
     * @param pageable pagination parameters including page number and size
     * @return a {@link Page} of feedback entries for the requested page
     */
    @Transactional(readOnly = true)
    public Page<Feedback> getAllFeedback(Pageable pageable) {
        return feedbackRepo.findAllByOrderByCreatedAtDesc(pageable);
    }

    /**
     * Retrieves feedback entries filtered by type with pagination.
     * 
     * <p>Allows administrators to view specific categories of feedback
     * (e.g., only bug reports or only feature requests).</p>
     * 
     * @param type     the {@link FeedbackType} to filter by
     * @param pageable pagination parameters
     * @return a {@link Page} of feedback entries of the specified type
     */
    @Transactional(readOnly = true)
    public Page<Feedback> getFeedbackByType(FeedbackType type, Pageable pageable) {
        return feedbackRepo.findByTypeOrderByCreatedAtDesc(type, pageable);
    }

    /**
     * Gets the count of unread feedback entries.
     * 
     * <p>Used for displaying notification badges in the admin interface
     * to indicate how many new feedback items need attention.</p>
     * 
     * @return the number of feedback entries that have not been marked as read
     */
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        return feedbackRepo.countUnread();
    }

    /**
     * Marks a feedback entry as read.
     * 
     * <p>Called when an administrator views or processes a feedback item.
     * If the feedback ID doesn't exist, the operation completes silently.</p>
     * 
     * @param feedbackId the unique identifier of the feedback to mark as read
     */
    @Transactional
    public void markAsRead(Long feedbackId) {
        feedbackRepo.findById(feedbackId).ifPresent(f -> {
            f.setRead(true);
            feedbackRepo.save(f);
        });
    }

    /**
     * Permanently deletes a feedback entry from the system.
     * 
     * <p>This operation cannot be undone. Use with caution.</p>
     * 
     * @param feedbackId the unique identifier of the feedback to delete
     */
    @Transactional
    public void deleteFeedback(Long feedbackId) {
        feedbackRepo.deleteById(feedbackId);
    }
}

package com.wipro.iaf.email.mail.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wipro.iaf.email.mail.entity.Email;

/**
 * Repository interface for managing {@link Email} entities.
 * <p>
 * This repository provides data access operations for email messages,
 * including retrieval of drafts, sent emails, and conversation threads.
 * It extends {@link JpaRepository} to inherit standard CRUD operations.
 * </p>
 *
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2025-01-16
 * @see Email
 * @see JpaRepository
 */
public interface EmailRepository extends JpaRepository<Email, Long> {

    /**
     * Retrieves a paginated list of draft emails for a specific sender.
     * <p>
     * Returns all emails marked as drafts (not yet sent) by the specified sender,
     * ordered by creation date in descending order (newest first).
     * </p>
     *
     * @param senderId the unique identifier of the sender/user
     * @param pageable pagination information including page number, size, and sorting
     * @return a {@link Page} containing draft emails for the specified sender
     */
    Page<Email> findBySenderIdAndDraftTrueOrderByCreatedAtDesc(
            Long senderId, Pageable pageable);

    /**
     * Retrieves a paginated list of sent emails (non-drafts) for a specific sender.
     * <p>
     * Returns all emails that have been sent (draft = false) by the specified sender,
     * ordered by creation date in descending order (newest first).
     * </p>
     *
     * @param senderId the unique identifier of the sender/user
     * @param pageable pagination information including page number, size, and sorting
     * @return a {@link Page} containing sent emails for the specified sender
     */
    Page<Email> findBySenderIdAndDraftFalseOrderByCreatedAtDesc(
            Long senderId, Pageable pageable);

    /**
     * Finds a specific email by its ID and sender ID.
     * <p>
     * This method ensures that only the sender of an email can access it
     * by requiring both the email ID and the sender's user ID.
     * </p>
     *
     * @param emailId the unique identifier of the email
     * @param userId the unique identifier of the sender/user
     * @return an {@link Optional} containing the email if found, or empty if not found
     */
    Optional<Email> findByIdAndSenderId(Long emailId, Long userId);

    /**
     * Finds all emails belonging to a specific conversation thread.
     * <p>
     * Retrieves all non-draft emails within a conversation thread, ordered by
     * creation date in ascending order to display the conversation chronologically.
     * </p>
     *
     * @param threadId the unique identifier of the conversation thread
     * @return a {@link List} of emails in the specified thread, ordered by creation date ascending
     */
    @Query("SELECT e FROM Email e WHERE e.threadId = :threadId AND e.draft = false ORDER BY e.createdAt ASC")
    List<Email> findByThreadId(@Param("threadId") Long threadId);
    
    /**
     * Counts the total number of sent emails (non-drafts) for a specific sender.
     * <p>
     * This method is typically used for tracking user achievements and statistics,
     * counting only emails that have actually been sent (excluding drafts).
     * </p>
     *
     * @param senderId the unique identifier of the sender/user
     * @return the count of sent emails for the specified sender
     */
    long countBySenderIdAndDraftFalse(Long senderId);
}

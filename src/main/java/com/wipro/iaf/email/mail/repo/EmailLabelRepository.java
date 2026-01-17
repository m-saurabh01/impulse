package com.wipro.iaf.email.mail.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wipro.iaf.email.mail.entity.EmailLabel;

/**
 * Repository interface for managing {@link EmailLabel} entities.
 * <p>
 * This repository handles the many-to-many relationship between emails and labels,
 * allowing users to apply custom labels to their emails for organization.
 * Each email-label association is user-specific, enabling personalized email categorization.
 * </p>
 *
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2025-01-16
 * @see EmailLabel
 * @see JpaRepository
 */
public interface EmailLabelRepository extends JpaRepository<EmailLabel, EmailLabel.EmailLabelId> {

    /**
     * Finds all email-label associations for a specific email and user.
     * <p>
     * Retrieves the basic association records without eager fetching related entities.
     * </p>
     *
     * @param emailId the unique identifier of the email
     * @param userId the unique identifier of the user
     * @return a {@link List} of email-label associations for the specified email and user
     */
    List<EmailLabel> findByEmailIdAndUserId(Long emailId, Long userId);

    /**
     * Finds all labels applied to a specific email for a user with eager fetching.
     * <p>
     * This method uses FETCH JOIN to load label details in a single query,
     * avoiding N+1 query problems when accessing label properties.
     * </p>
     *
     * @param emailId the unique identifier of the email
     * @param userId the unique identifier of the user
     * @return a {@link List} of email-label associations with labels eagerly fetched
     */
    @Query("SELECT el FROM EmailLabel el JOIN FETCH el.label WHERE el.email.id = :emailId AND el.userId = :userId")
    List<EmailLabel> findLabelsForEmail(@Param("emailId") Long emailId, @Param("userId") Long userId);

    /**
     * Retrieves a paginated list of emails associated with a specific label.
     * <p>
     * Returns emails with the given label for a user, ordered by creation date descending.
     * Uses FETCH JOIN to eagerly load email and sender details.
     * </p>
     *
     * @param labelId the unique identifier of the label
     * @param userId the unique identifier of the user
     * @param pageable pagination information including page number, size, and sorting
     * @return a {@link Page} of email-label associations for the specified label
     */
    @Query(value = "SELECT el FROM EmailLabel el JOIN FETCH el.email e JOIN FETCH e.sender WHERE el.label.id = :labelId AND el.userId = :userId ORDER BY e.createdAt DESC",
           countQuery = "SELECT COUNT(el) FROM EmailLabel el WHERE el.label.id = :labelId AND el.userId = :userId")
    Page<EmailLabel> findEmailsByLabel(@Param("labelId") Long labelId, @Param("userId") Long userId, Pageable pageable);

    /**
     * Counts the number of emails associated with a specific label for a user.
     * <p>
     * Useful for displaying label counts in the user interface or for statistics.
     * </p>
     *
     * @param labelId the unique identifier of the label
     * @param userId the unique identifier of the user
     * @return the count of emails with the specified label
     */
    @Query("SELECT COUNT(el) FROM EmailLabel el WHERE el.label.id = :labelId AND el.userId = :userId")
    long countByLabelIdAndUserId(@Param("labelId") Long labelId, @Param("userId") Long userId);

    /**
     * Removes a label from a specific email for a user.
     * <p>
     * This modifying operation deletes the association between an email and a label,
     * without deleting the email or the label themselves.
     * </p>
     *
     * @param emailId the unique identifier of the email
     * @param labelId the unique identifier of the label to remove
     * @param userId the unique identifier of the user
     */
    @Modifying
    @Query("DELETE FROM EmailLabel el WHERE el.email.id = :emailId AND el.label.id = :labelId AND el.userId = :userId")
    void removeLabel(@Param("emailId") Long emailId, @Param("labelId") Long labelId, @Param("userId") Long userId);

    /**
     * Deletes all email-label associations for a specific label.
     * <p>
     * This method is typically called when a label is being deleted,
     * to remove all references to that label from emails before the label itself is deleted.
     * </p>
     *
     * @param labelId the unique identifier of the label to remove from all emails
     */
    @Modifying
    @Query("DELETE FROM EmailLabel el WHERE el.label.id = :labelId")
    void deleteByLabelId(@Param("labelId") Long labelId);

    /**
     * Checks if a specific label is already applied to an email for a user.
     * <p>
     * Used to prevent duplicate label assignments and for validation purposes.
     * </p>
     *
     * @param emailId the unique identifier of the email
     * @param labelId the unique identifier of the label
     * @param userId the unique identifier of the user
     * @return {@code true} if the label is already applied to the email, {@code false} otherwise
     */
    boolean existsByEmailIdAndLabelIdAndUserId(Long emailId, Long labelId, Long userId);
}

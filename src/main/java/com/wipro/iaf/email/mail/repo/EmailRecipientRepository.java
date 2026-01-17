package com.wipro.iaf.email.mail.repo;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wipro.iaf.email.mail.entity.EmailRecipient;
import com.wipro.iaf.email.mail.entity.EmailRecipientId;

/**
 * Repository interface for managing {@link EmailRecipient} entities.
 * <p>
 * This repository handles the relationship between emails and their recipients,
 * providing operations for inbox, sent, trash, starred, and snoozed email management.
 * It supports various recipient types including TO, CC, BCC, and SENDER.
 * </p>
 *
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2025-01-16
 * @see EmailRecipient
 * @see EmailRecipientId
 * @see JpaRepository
 */
public interface EmailRecipientRepository
        extends JpaRepository<EmailRecipient, EmailRecipientId> {

    /**
     * Retrieves a paginated list of inbox emails for a specific user.
     * <p>
     * Returns non-deleted emails where the user is a recipient (not sender),
     * excluding drafts, ordered by creation date descending (newest first).
     * Uses FETCH JOIN to eagerly load email and sender details.
     * </p>
     *
     * @param userId the unique identifier of the user
     * @param pageable pagination information including page number, size, and sorting
     * @return a {@link Page} of inbox email recipients for the specified user
     */
	@Query(
		    value =
		        "SELECT r " +
		        "FROM EmailRecipient r " +
		        "JOIN FETCH r.email e " +
		        "JOIN FETCH e.sender s " +
		        "WHERE r.user.id = :userId " +
		        "AND r.deleted = false " +
		        "AND r.recipientType != 'SENDER' " +
		        "AND e.draft = false " +
		        "ORDER BY e.createdAt DESC",

		    countQuery =
		        "SELECT COUNT(r) " +
		        "FROM EmailRecipient r " +
		        "WHERE r.user.id = :userId " +
		        "AND r.deleted = false " +
		        "AND r.recipientType != 'SENDER' " +
		        "AND r.email.draft = false"
		)
		Page<EmailRecipient> findInbox(
		        @Param("userId") Long userId,
		        Pageable pageable);

    /**
     * Retrieves a paginated list of sent emails for a specific user.
     * <p>
     * Returns non-deleted emails where the user is marked as the sender (SENDER recipient type),
     * ordered by creation date descending. Uses FETCH JOIN to eagerly load email,
     * recipients, and user details.
     * </p>
     *
     * @param userId the unique identifier of the user
     * @param pageable pagination information including page number, size, and sorting
     * @return a {@link Page} of sent email recipients for the specified user
     */
	@Query(
	        value =
	            "SELECT r " +
	            "FROM EmailRecipient r " +
	            "JOIN FETCH r.email e " +
	            "LEFT JOIN FETCH e.recipients rec " +
	            "LEFT JOIN FETCH rec.user " +
	            "WHERE r.user.id = :userId " +
	            "AND r.deleted = false " +
	            "AND r.recipientType = 'SENDER' " +
	            "ORDER BY e.createdAt DESC",

	        countQuery =
	            "SELECT COUNT(r) " +
	            "FROM EmailRecipient r " +
	            "WHERE r.user.id = :userId " +
	            "AND r.deleted = false " +
	            "AND r.recipientType = 'SENDER'"
	    )
	    Page<EmailRecipient> findSent(
	            @Param("userId") Long userId,
	            Pageable pageable);

    /**
     * Retrieves a paginated list of deleted (trash) emails for a specific user.
     * <p>
     * Returns emails that have been marked as deleted by the user,
     * ordered by creation date descending. Uses FETCH JOIN to eagerly load
     * email and sender details.
     * </p>
     *
     * @param userId the unique identifier of the user
     * @param pageable pagination information including page number, size, and sorting
     * @return a {@link Page} of trashed email recipients for the specified user
     */
	@Query(
	        value =
	            "SELECT r " +
	            "FROM EmailRecipient r " +
	            "JOIN FETCH r.email e " +
	            "JOIN FETCH e.sender s " +
	            "WHERE r.user.id = :userId " +
	            "AND r.deleted = true " +
	            "ORDER BY e.createdAt DESC",

	        countQuery =
	            "SELECT COUNT(r) " +
	            "FROM EmailRecipient r " +
	            "WHERE r.user.id = :userId " +
	            "AND r.deleted = true"
	    )
	    Page<EmailRecipient> findTrash(
	            @Param("userId") Long userId,
	            Pageable pageable);
	
    /**
     * Checks if an email recipient record exists for the given email and user.
     *
     * @param emailId the unique identifier of the email
     * @param userId the unique identifier of the user
     * @return {@code true} if a recipient record exists, {@code false} otherwise
     */
	boolean existsByEmailIdAndUserId(Long emailId, Long userId);

    /**
     * Finds an email recipient record by email ID and user ID.
     * <p>
     * Retrieves the specific recipient record linking an email to a user,
     * useful for accessing recipient-specific properties like read status or starred flag.
     * </p>
     *
     * @param emailId the unique identifier of the email
     * @param userId the unique identifier of the user
     * @return an {@link Optional} containing the email recipient if found, or empty otherwise
     */
	Optional<EmailRecipient> findByEmailIdAndUserId(Long emailId, Long userId);

    /**
     * Deletes all recipient records associated with a specific email.
     * <p>
     * Typically used when permanently deleting an email to remove all
     * recipient associations.
     * </p>
     *
     * @param emailId the unique identifier of the email
     */
	void deleteByEmailId(Long emailId);

    /**
     * Permanently deletes all trashed emails for a specific user.
     * <p>
     * This modifying operation removes all email recipient records marked as deleted
     * for the specified user, effectively emptying their trash folder.
     * </p>
     *
     * @param userId the unique identifier of the user
     */
	@Modifying
	@Query("DELETE FROM EmailRecipient r WHERE r.user.id = :userId AND r.deleted = true")
	void deleteAllTrashedByUserId(@Param("userId") Long userId);

    /**
     * Searches inbox emails across multiple fields for a specific user.
     * <p>
     * Performs a case-insensitive search across email subject, body HTML,
     * sender email address, and sender display name. Returns paginated results
     * ordered by creation date descending.
     * </p>
     *
     * @param userId the unique identifier of the user
     * @param query the search query string to match against email fields
     * @param pageable pagination information including page number, size, and sorting
     * @return a {@link Page} of email recipients matching the search criteria
     */
	@Query(
	    value =
	        "SELECT r " +
	        "FROM EmailRecipient r " +
	        "JOIN FETCH r.email e " +
	        "JOIN FETCH e.sender s " +
	        "WHERE r.user.id = :userId " +
	        "AND r.deleted = false " +
	        "AND r.recipientType != 'SENDER' " +
	        "AND e.draft = false " +
	        "AND (LOWER(e.subject) LIKE LOWER(CONCAT('%', :query, '%')) " +
	        "     OR LOWER(e.bodyHtml) LIKE LOWER(CONCAT('%', :query, '%')) " +
	        "     OR LOWER(s.email) LIKE LOWER(CONCAT('%', :query, '%')) " +
	        "     OR LOWER(s.displayName) LIKE LOWER(CONCAT('%', :query, '%'))) " +
	        "ORDER BY e.createdAt DESC",

	    countQuery =
	        "SELECT COUNT(r) " +
	        "FROM EmailRecipient r " +
	        "WHERE r.user.id = :userId " +
	        "AND r.deleted = false " +
	        "AND r.recipientType != 'SENDER' " +
	        "AND r.email.draft = false " +
	        "AND (LOWER(r.email.subject) LIKE LOWER(CONCAT('%', :query, '%')) " +
	        "     OR LOWER(r.email.bodyHtml) LIKE LOWER(CONCAT('%', :query, '%')) " +
	        "     OR LOWER(r.email.sender.email) LIKE LOWER(CONCAT('%', :query, '%')) " +
	        "     OR LOWER(r.email.sender.displayName) LIKE LOWER(CONCAT('%', :query, '%')))"
	)
	Page<EmailRecipient> searchInbox(
	        @Param("userId") Long userId,
	        @Param("query") String query,
	        Pageable pageable);

    /**
     * Retrieves a paginated list of starred emails for a specific user.
     * <p>
     * Returns non-deleted emails that have been marked as starred by the user,
     * ordered by creation date descending. Uses FETCH JOIN to eagerly load
     * email and sender details.
     * </p>
     *
     * @param userId the unique identifier of the user
     * @param pageable pagination information including page number, size, and sorting
     * @return a {@link Page} of starred email recipients for the specified user
     */
	@Query(
	    value =
	        "SELECT r " +
	        "FROM EmailRecipient r " +
	        "JOIN FETCH r.email e " +
	        "JOIN FETCH e.sender s " +
	        "WHERE r.user.id = :userId " +
	        "AND r.deleted = false " +
	        "AND r.starred = true " +
	        "ORDER BY e.createdAt DESC",

	    countQuery =
	        "SELECT COUNT(r) " +
	        "FROM EmailRecipient r " +
	        "WHERE r.user.id = :userId " +
	        "AND r.deleted = false " +
	        "AND r.starred = true"
	)
	Page<EmailRecipient> findStarred(
	        @Param("userId") Long userId,
	        Pageable pageable);

    /**
     * Updates the starred status for an email recipient record.
     * <p>
     * This modifying operation toggles the starred flag for a specific
     * email-user combination, allowing users to mark/unmark emails as important.
     * </p>
     *
     * @param emailId the unique identifier of the email
     * @param userId the unique identifier of the user
     * @param starred the new starred status ({@code true} to star, {@code false} to unstar)
     */
	@Modifying
	@Query("UPDATE EmailRecipient r SET r.starred = :starred WHERE r.email.id = :emailId AND r.user.id = :userId")
	void updateStarred(@Param("emailId") Long emailId, @Param("userId") Long userId, @Param("starred") boolean starred);

    /**
     * Counts the number of unread emails in a user's inbox.
     * <p>
     * Returns the count of non-deleted, non-draft emails where the user is a recipient
     * (not sender) and the email has not been read. Useful for displaying unread badge counts.
     * </p>
     *
     * @param userId the unique identifier of the user
     * @return the count of unread inbox emails for the specified user
     */
	@Query(
	    "SELECT COUNT(r) " +
	    "FROM EmailRecipient r " +
	    "WHERE r.user.id = :userId " +
	    "AND r.deleted = false " +
	    "AND r.recipientType != 'SENDER' " +
	    "AND r.email.draft = false " +
	    "AND r.read = false"
	)
	long countUnreadInbox(@Param("userId") Long userId);

    /**
     * Counts the total number of starred emails for a specific user.
     * <p>
     * This method is typically used for tracking user achievements and statistics,
     * counting all emails that the user has marked as starred.
     * </p>
     *
     * @param userId the unique identifier of the user
     * @return the count of starred emails for the specified user
     */
	@Query("SELECT COUNT(r) FROM EmailRecipient r WHERE r.user.id = :userId AND r.starred = true")
	long countStarredByUserId(@Param("userId") Long userId);

    /**
     * Updates the snooze time for an email recipient record.
     * <p>
     * This modifying operation sets the snooze until date/time for a specific
     * email-user combination, temporarily hiding the email until the specified time.
     * </p>
     *
     * @param emailId the unique identifier of the email
     * @param userId the unique identifier of the user
     * @param snoozedUntil the date and time until which the email should be snoozed
     */
	@Modifying
	@Query("UPDATE EmailRecipient r SET r.snoozedUntil = :snoozedUntil WHERE r.email.id = :emailId AND r.user.id = :userId")
	void updateSnooze(@Param("emailId") Long emailId, @Param("userId") Long userId, @Param("snoozedUntil") LocalDateTime snoozedUntil);

    /**
     * Retrieves a paginated list of snoozed emails for a specific user.
     * <p>
     * Returns non-deleted, non-draft emails where the user is a recipient and
     * the snooze time is still active (snooze until date is in the future).
     * Results are ordered by snooze end time ascending (earliest snooze ends first).
     * </p>
     *
     * @param userId the unique identifier of the user
     * @param now the current date/time to compare against snooze end times
     * @param pageable pagination information including page number, size, and sorting
     * @return a {@link Page} of snoozed email recipients for the specified user
     */
	@Query(
	    value =
	        "SELECT r " +
	        "FROM EmailRecipient r " +
	        "JOIN FETCH r.email e " +
	        "JOIN FETCH e.sender s " +
	        "WHERE r.user.id = :userId " +
	        "AND r.deleted = false " +
	        "AND r.recipientType != 'SENDER' " +
	        "AND e.draft = false " +
	        "AND r.snoozedUntil IS NOT NULL " +
	        "AND r.snoozedUntil > :now " +
	        "ORDER BY r.snoozedUntil ASC",
	    countQuery =
	        "SELECT COUNT(r) " +
	        "FROM EmailRecipient r " +
	        "WHERE r.user.id = :userId " +
	        "AND r.deleted = false " +
	        "AND r.recipientType != 'SENDER' " +
	        "AND r.email.draft = false " +
	        "AND r.snoozedUntil IS NOT NULL " +
	        "AND r.snoozedUntil > :now"
	)
	Page<EmailRecipient> findSnoozed(
	        @Param("userId") Long userId,
	        @Param("now") LocalDateTime now,
	        Pageable pageable);

    /**
     * Counts the number of snoozed emails for a specific user.
     * <p>
     * Returns the count of non-deleted, non-draft emails where the user is a recipient
     * and the snooze is still active. Useful for displaying snoozed email counts.
     * </p>
     *
     * @param userId the unique identifier of the user
     * @param now the current date/time to compare against snooze end times
     * @return the count of snoozed emails for the specified user
     */
	@Query(
	    "SELECT COUNT(r) " +
	    "FROM EmailRecipient r " +
	    "WHERE r.user.id = :userId " +
	    "AND r.deleted = false " +
	    "AND r.recipientType != 'SENDER' " +
	    "AND r.email.draft = false " +
	    "AND r.snoozedUntil IS NOT NULL " +
	    "AND r.snoozedUntil > :now"
	)
	long countSnoozed(@Param("userId") Long userId, @Param("now") LocalDateTime now);

}

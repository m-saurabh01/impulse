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

public interface EmailRecipientRepository
        extends JpaRepository<EmailRecipient, EmailRecipientId> {

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
	
	boolean existsByEmailIdAndUserId(Long emailId, Long userId);


	Optional<EmailRecipient> findByEmailIdAndUserId(Long emailId, Long userId);

	void deleteByEmailId(Long emailId);

	@Modifying
	@Query("DELETE FROM EmailRecipient r WHERE r.user.id = :userId AND r.deleted = true")
	void deleteAllTrashedByUserId(@Param("userId") Long userId);

	// Search across inbox (subject, body, sender email, sender name)
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

	// Find starred emails
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

	// Toggle star
	@Modifying
	@Query("UPDATE EmailRecipient r SET r.starred = :starred WHERE r.email.id = :emailId AND r.user.id = :userId")
	void updateStarred(@Param("emailId") Long emailId, @Param("userId") Long userId, @Param("starred") boolean starred);

	// Count unread emails in inbox
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
	 * Count starred emails for a user (for achievements)
	 */
	@Query("SELECT COUNT(r) FROM EmailRecipient r WHERE r.user.id = :userId AND r.starred = true")
	long countStarredByUserId(@Param("userId") Long userId);

	/**
	 * Update snooze time for an email
	 */
	@Modifying
	@Query("UPDATE EmailRecipient r SET r.snoozedUntil = :snoozedUntil WHERE r.email.id = :emailId AND r.user.id = :userId")
	void updateSnooze(@Param("emailId") Long emailId, @Param("userId") Long userId, @Param("snoozedUntil") LocalDateTime snoozedUntil);

	/**
	 * Get snoozed emails for a user (where snooze is still active)
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
	 * Count snoozed emails for a user
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

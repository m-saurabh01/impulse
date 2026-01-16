package com.wipro.iaf.email.mail.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wipro.iaf.email.mail.entity.Email;

public interface EmailRepository extends JpaRepository<Email, Long> {

    
    Page<Email> findBySenderIdAndDraftTrueOrderByCreatedAtDesc(
            Long senderId, Pageable pageable);

    Page<Email> findBySenderIdAndDraftFalseOrderByCreatedAtDesc(
            Long senderId, Pageable pageable);

	Optional<Email> findByIdAndSenderId(Long emailId, Long userId);

    /**
     * Find all emails in a conversation thread
     */
    @Query("SELECT e FROM Email e WHERE e.threadId = :threadId AND e.draft = false ORDER BY e.createdAt ASC")
    List<Email> findByThreadId(@Param("threadId") Long threadId);
    
    /**
     * Count emails sent by a user (non-draft, for achievements)
     */
    long countBySenderIdAndDraftFalse(Long senderId);
}

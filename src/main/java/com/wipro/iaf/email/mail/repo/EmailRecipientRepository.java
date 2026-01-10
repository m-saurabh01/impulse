package com.wipro.iaf.email.mail.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
		        "AND e.draft = false " +
		        "ORDER BY e.createdAt DESC",

		    countQuery =
		        "SELECT COUNT(r) " +
		        "FROM EmailRecipient r " +
		        "WHERE r.user.id = :userId " +
		        "AND r.deleted = false " +
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

}

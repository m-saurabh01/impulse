package com.wipro.iaf.email.mail.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wipro.iaf.email.mail.entity.Email;

public interface EmailRepository extends JpaRepository<Email, Long> {

    
    Page<Email> findBySenderIdAndDraftTrueOrderByCreatedAtDesc(
            Long senderId, Pageable pageable);

    Page<Email> findBySenderIdAndDraftFalseOrderByCreatedAtDesc(
            Long senderId, Pageable pageable);

	Optional<Email> findByIdAndSenderId(Long emailId, Long userId);
}

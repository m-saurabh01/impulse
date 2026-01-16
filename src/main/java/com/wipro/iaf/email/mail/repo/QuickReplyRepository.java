package com.wipro.iaf.email.mail.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wipro.iaf.email.mail.entity.QuickReply;

public interface QuickReplyRepository extends JpaRepository<QuickReply, Long> {

    List<QuickReply> findByUserIdOrderBySortOrderAsc(Long userId);

    Optional<QuickReply> findByIdAndUserId(Long id, Long userId);

    void deleteByIdAndUserId(Long id, Long userId);

    long countByUserId(Long userId);
}

package com.wipro.iaf.email.mail.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wipro.iaf.email.mail.entity.EmailLabel;

public interface EmailLabelRepository extends JpaRepository<EmailLabel, EmailLabel.EmailLabelId> {

    List<EmailLabel> findByEmailIdAndUserId(Long emailId, Long userId);

    @Query("SELECT el FROM EmailLabel el JOIN FETCH el.label WHERE el.email.id = :emailId AND el.userId = :userId")
    List<EmailLabel> findLabelsForEmail(@Param("emailId") Long emailId, @Param("userId") Long userId);

    @Query(value = "SELECT el FROM EmailLabel el JOIN FETCH el.email e JOIN FETCH e.sender WHERE el.label.id = :labelId AND el.userId = :userId ORDER BY e.createdAt DESC",
           countQuery = "SELECT COUNT(el) FROM EmailLabel el WHERE el.label.id = :labelId AND el.userId = :userId")
    Page<EmailLabel> findEmailsByLabel(@Param("labelId") Long labelId, @Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(el) FROM EmailLabel el WHERE el.label.id = :labelId AND el.userId = :userId")
    long countByLabelIdAndUserId(@Param("labelId") Long labelId, @Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM EmailLabel el WHERE el.email.id = :emailId AND el.label.id = :labelId AND el.userId = :userId")
    void removeLabel(@Param("emailId") Long emailId, @Param("labelId") Long labelId, @Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM EmailLabel el WHERE el.label.id = :labelId")
    void deleteByLabelId(@Param("labelId") Long labelId);

    boolean existsByEmailIdAndLabelIdAndUserId(Long emailId, Long labelId, Long userId);
}

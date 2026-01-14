package com.wipro.iaf.email.common.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wipro.iaf.email.common.entity.Feedback;
import com.wipro.iaf.email.common.entity.Feedback.FeedbackType;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Page<Feedback> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Feedback> findByTypeOrderByCreatedAtDesc(FeedbackType type, Pageable pageable);

    List<Feedback> findByReadFalseOrderByCreatedAtDesc();

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.read = false")
    long countUnread();

    @Query("SELECT f.type, COUNT(f) FROM Feedback f GROUP BY f.type")
    List<Object[]> countByType();
}

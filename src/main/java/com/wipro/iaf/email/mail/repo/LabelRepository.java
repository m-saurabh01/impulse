package com.wipro.iaf.email.mail.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wipro.iaf.email.mail.entity.Label;

public interface LabelRepository extends JpaRepository<Label, Long> {

    List<Label> findByUserIdOrderByNameAsc(Long userId);

    boolean existsByUserIdAndNameIgnoreCase(Long userId, String name);

    void deleteByIdAndUserId(Long id, Long userId);
    
    /**
     * Count labels created by a user (for achievements)
     */
    long countByUserId(Long userId);
}

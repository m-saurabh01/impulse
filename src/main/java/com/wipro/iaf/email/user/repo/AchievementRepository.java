package com.wipro.iaf.email.user.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wipro.iaf.email.user.entity.Achievement;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    
    Optional<Achievement> findByCode(String code);
    
    List<Achievement> findAllByOrderBySortOrderAsc();
    
    List<Achievement> findByCategoryOrderBySortOrderAsc(Achievement.AchievementCategory category);
}

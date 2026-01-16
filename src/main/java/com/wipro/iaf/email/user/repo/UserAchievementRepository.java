package com.wipro.iaf.email.user.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wipro.iaf.email.user.entity.UserAchievement;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    
    @Query("SELECT ua FROM UserAchievement ua JOIN FETCH ua.achievement WHERE ua.user.id = :userId ORDER BY ua.unlockedAt DESC")
    List<UserAchievement> findByUserIdWithAchievements(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(ua) FROM UserAchievement ua WHERE ua.user.id = :userId")
    int countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(ua.achievement.points) FROM UserAchievement ua WHERE ua.user.id = :userId")
    Integer getTotalPointsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT ua FROM UserAchievement ua WHERE ua.user.id = :userId AND ua.achievement.code = :code")
    Optional<UserAchievement> findByUserIdAndAchievementCode(@Param("userId") Long userId, @Param("code") String code);
    
    boolean existsByUserIdAndAchievementCode(Long userId, String code);
    
    @Query("SELECT ua FROM UserAchievement ua JOIN FETCH ua.achievement WHERE ua.user.id = :userId AND ua.notified = false")
    List<UserAchievement> findUnnotifiedByUserId(@Param("userId") Long userId);
    
    @Modifying
    @Query("UPDATE UserAchievement ua SET ua.notified = true WHERE ua.user.id = :userId AND ua.notified = false")
    void markAllNotified(@Param("userId") Long userId);
}

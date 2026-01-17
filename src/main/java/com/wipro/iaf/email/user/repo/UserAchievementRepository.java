package com.wipro.iaf.email.user.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wipro.iaf.email.user.entity.UserAchievement;

/**
 * Repository interface for {@link UserAchievement} entity database operations.
 * 
 * <p>Manages the relationship between users and their unlocked achievements.
 * Provides methods for tracking achievement progress, notifications, and
 * calculating user points for leaderboards.</p>
 * 
 * <p>Key features:
 * <ul>
 *   <li>Retrieve a user's unlocked achievements with full details</li>
 *   <li>Check if user has already unlocked specific achievements</li>
 *   <li>Calculate total points for leaderboard rankings</li>
 *   <li>Manage notification status for new achievement popups</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see UserAchievement
 * @see com.wipro.iaf.email.user.entity.Achievement
 */
@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {
    
    /**
     * Retrieves all achievements unlocked by a user with full achievement details.
     * 
     * <p>Uses JOIN FETCH to eagerly load achievement data, avoiding N+1 queries.
     * Results are ordered by unlock date with most recent first.</p>
     * 
     * @param userId the ID of the user whose achievements to retrieve
     * @return list of user achievements with full achievement details, newest first
     */
    @Query("SELECT ua FROM UserAchievement ua JOIN FETCH ua.achievement WHERE ua.user.id = :userId ORDER BY ua.unlockedAt DESC")
    List<UserAchievement> findByUserIdWithAchievements(@Param("userId") Long userId);
    
    /**
     * Counts the total number of achievements unlocked by a user.
     * 
     * <p>Used for displaying achievement count in profiles and statistics.</p>
     * 
     * @param userId the ID of the user
     * @return the count of achievements unlocked by the user
     */
    @Query("SELECT COUNT(ua) FROM UserAchievement ua WHERE ua.user.id = :userId")
    int countByUserId(@Param("userId") Long userId);
    
    /**
     * Calculates the total achievement points earned by a user.
     * 
     * <p>Sums the point values of all unlocked achievements for leaderboard rankings.</p>
     * 
     * @param userId the ID of the user
     * @return total points earned, or null if user has no achievements
     */
    @Query("SELECT SUM(ua.achievement.points) FROM UserAchievement ua WHERE ua.user.id = :userId")
    Integer getTotalPointsByUserId(@Param("userId") Long userId);
    
    /**
     * Finds a specific user-achievement record by user ID and achievement code.
     * 
     * <p>Used to check progress or retrieve details for a specific achievement.</p>
     * 
     * @param userId the ID of the user
     * @param code   the unique code of the achievement
     * @return an {@link Optional} containing the record if user has unlocked it
     */
    @Query("SELECT ua FROM UserAchievement ua WHERE ua.user.id = :userId AND ua.achievement.code = :code")
    Optional<UserAchievement> findByUserIdAndAchievementCode(@Param("userId") Long userId, @Param("code") String code);
    
    /**
     * Checks if a user has already unlocked a specific achievement.
     * 
     * <p>Used before attempting to grant an achievement to avoid duplicates.</p>
     * 
     * @param userId the ID of the user
     * @param code   the unique code of the achievement to check
     * @return true if user has already unlocked this achievement
     */
    boolean existsByUserIdAndAchievementCode(Long userId, String code);
    
    /**
     * Retrieves all achievements that user hasn't been notified about yet.
     * 
     * <p>Used to display new achievement popups when user logs in or
     * performs actions that unlock achievements.</p>
     * 
     * @param userId the ID of the user
     * @return list of unnotified achievements with full details
     */
    @Query("SELECT ua FROM UserAchievement ua JOIN FETCH ua.achievement WHERE ua.user.id = :userId AND ua.notified = false")
    List<UserAchievement> findUnnotifiedByUserId(@Param("userId") Long userId);
    
    /**
     * Marks all of a user's achievements as notified.
     * 
     * <p>Called after displaying achievement notifications to prevent
     * showing the same achievements again.</p>
     * 
     * @param userId the ID of the user whose achievements to mark as notified
     */
    @Modifying
    @Query("UPDATE UserAchievement ua SET ua.notified = true WHERE ua.user.id = :userId AND ua.notified = false")
    void markAllNotified(@Param("userId") Long userId);
}

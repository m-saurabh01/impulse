package com.wipro.iaf.email.user.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity tracking which achievements a user has unlocked and when.
 * 
 * <p>This entity represents the many-to-many relationship between users and
 * achievements, storing additional metadata like unlock timestamp, notification
 * status, and progress tracking. Each user-achievement combination is unique.</p>
 * 
 * <p>Key features:
 * <ul>
 *   <li>Tracks when each achievement was unlocked</li>
 *   <li>Manages notification state to show new achievement popups once</li>
 *   <li>Stores progress towards milestone-based achievements</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see User
 * @see Achievement
 */
@Entity
@Table(name = "user_achievements", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "achievement_id"}),
       indexes = @Index(name = "idx_user_achievements_user", columnList = "user_id"))
@Data
@NoArgsConstructor
public class UserAchievement {

    /** Unique identifier for this user-achievement record */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who unlocked this achievement */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** The achievement that was unlocked (eagerly loaded for display) */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;

    /** Timestamp when the achievement was unlocked */
    @Column(name = "unlocked_at", nullable = false)
    private LocalDateTime unlockedAt;

    /** Flag indicating whether user has seen the unlock notification */
    @Column(nullable = false)
    private boolean notified = false;

    /** Current progress count towards milestone achievements (e.g., 7/10 emails sent) */
    @Column(name = "current_progress")
    private int currentProgress;

    /**
     * JPA lifecycle callback executed before persisting a new record.
     * Automatically sets the unlock timestamp if not already set.
     */
    @PrePersist
    protected void onCreate() {
        if (unlockedAt == null) {
            unlockedAt = LocalDateTime.now();
        }
    }

    /**
     * Constructs a new UserAchievement linking a user to an unlocked achievement.
     * 
     * @param user        the user who unlocked the achievement
     * @param achievement the achievement that was unlocked
     */
    public UserAchievement(User user, Achievement achievement) {
        this.user = user;
        this.achievement = achievement;
        this.unlockedAt = LocalDateTime.now();
    }
}

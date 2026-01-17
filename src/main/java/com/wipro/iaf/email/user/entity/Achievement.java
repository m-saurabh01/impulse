package com.wipro.iaf.email.user.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity defining available achievements/badges that users can unlock in PulseMail.
 * 
 * <p>Achievements gamify the email experience by rewarding users for various
 * activities like sending emails, organizing with labels, and engaging with
 * the platform. Each achievement has a visual badge with icon and color,
 * along with point values for leaderboards.</p>
 * 
 * <p>Achievement categories:
 * <ul>
 *   <li>{@link AchievementCategory#SENDING} - Rewards for sending emails</li>
 *   <li>{@link AchievementCategory#RECEIVING} - Rewards for receiving emails</li>
 *   <li>{@link AchievementCategory#ORGANIZING} - Rewards for using labels and organization features</li>
 *   <li>{@link AchievementCategory#ENGAGEMENT} - Rewards for replies, stars, and active usage</li>
 *   <li>{@link AchievementCategory#SPECIAL} - Easter eggs and special event rewards</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see UserAchievement
 * @see AchievementCategory
 */
@Entity
@Table(name = "achievements")
@Data
@NoArgsConstructor
public class Achievement {

    /** Unique identifier for the achievement definition */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique code identifier used for programmatic references (e.g., "FIRST_EMAIL", "INBOX_ZERO") */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    /** Human-readable name displayed in the UI (e.g., "First Steps", "Inbox Zero") */
    @Column(nullable = false, length = 100)
    private String name;

    /** Detailed description explaining how to unlock the achievement */
    @Column(length = 255)
    private String description;

    /** Bootstrap icon class name for visual representation (e.g., "bi-trophy", "bi-star") */
    @Column(nullable = false, length = 50)
    private String icon;

    /** Hex color code for the badge background (e.g., "#FFD700" for gold) */
    @Column(nullable = false, length = 20)
    private String color;

    /** Category grouping for organizing achievements in the UI */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementCategory category;

    /** Number of actions required to unlock (e.g., 10 emails sent, 5 labels created) */
    @Column(nullable = false)
    private int threshold;

    /** Points awarded when achievement is unlocked, used for leaderboards */
    @Column(nullable = false)
    private int points;

    /** Display order within category for consistent UI presentation */
    @Column(name = "sort_order")
    private int sortOrder;

    /**
     * Categories for grouping achievements by activity type.
     * 
     * <p>Categories help organize achievements in the UI and allow
     * filtering by activity type in the achievements display.</p>
     */
    public enum AchievementCategory {
        /** Achievements related to sending emails (first email, milestones, etc.) */
        SENDING,
        /** Achievements related to receiving and reading emails */
        RECEIVING,
        /** Achievements for organizing emails with labels, folders, etc. */
        ORGANIZING,
        /** Achievements for engagement activities like replies and starring */
        ENGAGEMENT,
        /** Easter eggs and special event achievements */
        SPECIAL
    }

    /**
     * Constructs a new Achievement with all required attributes.
     * 
     * @param code        unique code identifier for programmatic reference
     * @param name        human-readable name for display
     * @param description detailed description of how to unlock
     * @param icon        Bootstrap icon class name
     * @param color       hex color code for badge background
     * @param category    category for grouping
     * @param threshold   number of actions required to unlock
     * @param points      points awarded when unlocked
     */
    public Achievement(String code, String name, String description, String icon, 
                       String color, AchievementCategory category, int threshold, int points) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.color = color;
        this.category = category;
        this.threshold = threshold;
        this.points = points;
    }
}

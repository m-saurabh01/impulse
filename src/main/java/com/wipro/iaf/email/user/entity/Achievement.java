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
 * Defines available achievements/badges in the system.
 */
@Entity
@Table(name = "achievements")
@Data
@NoArgsConstructor
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false, length = 50)
    private String icon;  // Bootstrap icon class name

    @Column(nullable = false, length = 20)
    private String color;  // Hex color for badge

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementCategory category;

    @Column(nullable = false)
    private int threshold;  // Number required to unlock

    @Column(nullable = false)
    private int points;  // Points awarded

    @Column(name = "sort_order")
    private int sortOrder;

    public enum AchievementCategory {
        SENDING,      // Email sending achievements
        RECEIVING,    // Email receiving achievements
        ORGANIZING,   // Labels, folders, etc.
        ENGAGEMENT,   // Replies, stars, etc.
        SPECIAL       // Easter eggs, special events
    }

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

package com.wipro.iaf.email.user.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.Data;

/**
 * Entity representing a registered user in the PulseMail system.
 * 
 * <p>Users are the primary actors in the email system. Each user has a unique
 * email address that serves as their username and mailbox identifier. Users can
 * send/receive emails, manage contacts, organize with labels, and earn achievements.</p>
 * 
 * <p>Key attributes:
 * <ul>
 *   <li><b>email</b> - Unique identifier and login credential</li>
 *   <li><b>passwordHash</b> - BCrypt-encoded password for secure authentication</li>
 *   <li><b>role</b> - Authorization role (USER, ADMIN)</li>
 *   <li><b>displayName</b> - Human-readable name shown in email headers</li>
 *   <li><b>signature</b> - Optional HTML signature appended to outgoing emails</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see Contact
 * @see Achievement
 * @see UserAchievement
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email")
})
@Data
public class User {

    /** Unique identifier for the user */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique email address serving as username and mailbox identifier */
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /** BCrypt-encoded password hash for secure authentication */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /** Authorization role: USER for standard users, ADMIN for administrators */
    @Column(nullable = false)
    private String role;

    /** Flag indicating whether the account is active and can log in */
    @Column(nullable = false)
    private boolean enabled = true;
    
    /** Flag indicating whether the account has been deleted (soft-delete) */
    @Column(nullable = false)
    private boolean deleted = false;

    /** Human-readable display name shown in email headers and UI */
    @Column(name = "display_name", length = 100)
    private String displayName;

    /** Optional HTML signature appended to outgoing emails */
    @Column(name = "signature", columnDefinition = "TEXT")
    private String signature;

    /** Timestamp when the user account was created */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * JPA lifecycle callback executed before persisting a new user.
     * Automatically sets the creation timestamp if not already set.
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    /**
     * Gets the user's display name with fallback to email prefix.
     * 
     * <p>If no display name is set, extracts the portion of the email
     * address before the @ symbol as a fallback display name.</p>
     * 
     * @return display name if set, otherwise the email prefix before @
     */
    public String getDisplayNameOrEmail() {
        if (displayName != null && !displayName.trim().isEmpty()) {
            return displayName;
        }
        // Extract name from email (before @)
        if (email != null && email.contains("@")) {
            return email.substring(0, email.indexOf("@"));
        }
        return email;
    }
}

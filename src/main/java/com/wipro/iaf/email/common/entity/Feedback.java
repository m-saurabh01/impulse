package com.wipro.iaf.email.common.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.wipro.iaf.email.user.entity.User;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing user feedback, suggestions, bug reports, and feature requests.
 * 
 * <p>This entity stores all types of user-submitted feedback to help improve
 * the PulseMail application. Each feedback entry is associated with the submitting
 * user (if logged in) and contains details about the type, subject, and message.</p>
 * 
 * <p>Feedback types include:
 * <ul>
 *   <li>{@link FeedbackType#FEEDBACK} - General feedback about the application</li>
 *   <li>{@link FeedbackType#SUGGESTION} - Suggestions for improvements</li>
 *   <li>{@link FeedbackType#BUG_REPORT} - Reports of bugs or issues</li>
 *   <li>{@link FeedbackType#FEATURE_REQUEST} - Requests for new features</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 */
@Entity
@Table(name = "feedback")
@Data
@NoArgsConstructor
public class Feedback {

    /**
     * Enumeration of feedback types supported by the system.
     * 
     * <p>These types help categorize and prioritize user submissions
     * for efficient processing by administrators.</p>
     */
    public enum FeedbackType {
        /** General feedback about the application experience */
        FEEDBACK,
        /** Suggestions for improvements or enhancements */
        SUGGESTION,
        /** Reports of bugs, errors, or unexpected behavior */
        BUG_REPORT,
        /** Requests for new features or functionality */
        FEATURE_REQUEST
    }

    /** Unique identifier for the feedback entry */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who submitted the feedback (null if submitted anonymously or before login) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /** Name of the person submitting feedback */
    @Column(nullable = false, length = 100)
    private String name;

    /** Email address for follow-up communication */
    @Column(nullable = false, length = 255)
    private String email;

    /** Category/type of the feedback */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FeedbackType type;

    /** Brief subject line describing the feedback */
    @Column(nullable = false, length = 100)
    private String subject;

    /** Detailed message content of the feedback */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    /** Flag indicating whether the feedback has been reviewed by an administrator */
    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    /** Timestamp when the feedback was submitted */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * JPA lifecycle callback executed before persisting a new feedback entry.
     * Automatically sets the creation timestamp to the current date/time.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Constructs a new Feedback instance with the specified details.
     * 
     * @param user    the user submitting the feedback (can be null for anonymous submissions)
     * @param name    the name of the person submitting feedback
     * @param email   the email address for follow-up communication
     * @param type    the category of feedback (FEEDBACK, SUGGESTION, BUG_REPORT, FEATURE_REQUEST)
     * @param subject a brief subject line describing the feedback
     * @param message the detailed message content
     */
    public Feedback(User user, String name, String email, FeedbackType type, String subject, String message) {
        this.user = user;
        this.name = name;
        this.email = email;
        this.type = type;
        this.subject = subject;
        this.message = message;
    }
}

package com.wipro.iaf.email.mail.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wipro.iaf.email.user.entity.User;

import lombok.Data;

/**
 * Entity representing a user's reusable quick reply template.
 * 
 * <p>Quick replies allow users to save frequently used text snippets that
 * can be quickly inserted into email compositions. Each user can have up
 * to 10 quick replies that are stored and ordered by sortOrder.</p>
 * 
 * <p>Quick replies are displayed in a collapsible panel in the compose view
 * and can be inserted into the email body with a single click.</p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see User
 */
@Entity
@Table(name = "quick_replies")
@Data
public class QuickReply {

    /** Unique identifier for the quick reply */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who owns this quick reply (excluded from JSON serialization) */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    /** Short title for identifying the quick reply (max 100 characters) */
    @Column(nullable = false, length = 100)
    private String title;

    /** The actual text content to be inserted when using this quick reply */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** Display order for organizing quick replies in the UI */
    private Integer sortOrder = 0;

    /** Timestamp when the quick reply was created */
    private LocalDateTime createdAt = LocalDateTime.now();

    /** Timestamp when the quick reply was last modified */
    private LocalDateTime updatedAt = LocalDateTime.now();
}

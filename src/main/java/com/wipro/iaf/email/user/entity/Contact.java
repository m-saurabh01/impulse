package com.wipro.iaf.email.user.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * Entity representing a contact in a user's personal address book.
 * 
 * <p>Contacts allow users to maintain a personal directory of email addresses
 * with associated metadata like display names, phone numbers, and notes.
 * Contacts can be marked as favorites for quick access.</p>
 * 
 * <p>Each contact belongs to exactly one user (owner) and stores information
 * about a single email address. The same external email can appear in multiple
 * users' contact lists with different metadata.</p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see User
 */
@Entity
@Table(name = "contacts", indexes = {
    @Index(name = "idx_contact_user", columnList = "user_id"),
    @Index(name = "idx_contact_email", columnList = "contact_email")
})
@Data
public class Contact {

    /** Unique identifier for the contact entry */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who owns this contact entry */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /** Email address of the contact */
    @Column(name = "contact_email", nullable = false, length = 255)
    private String email;

    /** Human-readable display name for the contact */
    @Column(name = "display_name", length = 100)
    private String displayName;

    /** Optional phone number for the contact */
    @Column(length = 20)
    private String phone;

    /** Optional company/organization name */
    @Column(length = 100)
    private String company;

    /** Optional free-form notes about the contact */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /** Flag indicating if contact is marked as favorite for quick access */
    @Column(name = "is_favorite")
    private boolean favorite;

    /** Timestamp when the contact was created */
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Default constructor required by JPA.
     */
    public Contact() {}

    /**
     * Constructs a new Contact with essential information.
     * 
     * @param user        the user who owns this contact
     * @param email       the email address of the contact
     * @param displayName the human-readable name for the contact
     */
    public Contact(User user, String email, String displayName) {
        this.user = user;
        this.email = email;
        this.displayName = displayName;
    }

    /**
     * Gets the contact's display name with fallback to email.
     * 
     * <p>Returns the display name if set, otherwise returns the email
     * address as a fallback identifier.</p>
     * 
     * @return the display name if set, otherwise the email address
     */
    public String getDisplayNameOrEmail() {
        if (displayName != null && !displayName.trim().isEmpty()) {
            return displayName;
        }
        return email;
    }
}

package com.wipro.iaf.email.mail.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.wipro.iaf.email.user.entity.User;

import lombok.Data;

/**
 * Entity representing an email message in the PulseMail system.
 * 
 * <p>This is the core entity of the email system, representing a single email
 * message. Each email has exactly one sender and can have multiple recipients
 * of different types (TO, CC, BCC). Emails can be drafts or sent messages.</p>
 * 
 * <p>Key features:
 * <ul>
 *   <li><b>Threading</b> - Related emails share a threadId for conversation view</li>
 *   <li><b>Rich Content</b> - Body is stored as HTML for formatting support</li>
 *   <li><b>Draft Support</b> - Emails can be saved as drafts before sending</li>
 *   <li><b>Read Receipts</b> - Sender can request notification when email is read</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see EmailRecipient
 * @see User
 */
@Entity
@Table(name = "emails", indexes = {
    @Index(name = "idx_email_sender", columnList = "sender_id"),
    @Index(name = "idx_email_thread", columnList = "thread_id"),
    @Index(name = "idx_email_draft", columnList = "is_draft"),
    @Index(name = "idx_email_created", columnList = "created_at DESC"),
    @Index(name = "idx_email_sender_draft", columnList = "sender_id, is_draft")
})
@Data
public class Email {

    /** Unique identifier for the email */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who sent or is composing this email */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User sender;

    /** Email subject line (max 500 characters) */
    @Column(length = 500)
    private String subject;

    /** Email body content in HTML format for rich text support */
    @Lob
    @Column(name = "body_html")
    private String bodyHtml;

    /** Thread ID linking related emails in a conversation */
    @Column(name = "thread_id")
    private Long threadId;

    /** Flag indicating if this is a draft (not yet sent) */
    @Column(name = "is_draft")
    private boolean draft;

    /** Flag indicating if sender wants read receipt notification */
    @Column(name = "read_receipt_requested")
    private Boolean readReceiptRequested = false;

    /**
     * Checks if read receipt was requested for this email.
     * 
     * @return true if sender requested read receipt notification
     */
    public boolean isReadReceiptRequested() {
        return readReceiptRequested != null && readReceiptRequested;
    }

    /** Timestamp when the email was created (draft) or sent */
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    /** List of recipients (TO, CC, BCC) for this email */
    @OneToMany(mappedBy = "email", fetch = FetchType.LAZY)
    private List<EmailRecipient> recipients = new ArrayList<>();

    /**
     * Gets comma-separated list of TO recipient email addresses.
     * 
     * @return comma-separated TO recipient emails, or empty string if none
     */
    public String getToRecipients() {
        return recipients.stream()
            .filter(r -> "TO".equals(r.getRecipientType()))
            .map(r -> r.getUser().getEmail())
            .collect(Collectors.joining(", "));
    }

    /**
     * Gets comma-separated list of CC recipient email addresses.
     * 
     * @return comma-separated CC recipient emails, or empty string if none
     */
    public String getCcRecipients() {
        return recipients.stream()
            .filter(r -> "CC".equals(r.getRecipientType()))
            .map(r -> r.getUser().getEmail())
            .collect(Collectors.joining(", "));
    }

    /**
     * Gets comma-separated list of BCC recipient email addresses.
     * 
     * @return comma-separated BCC recipient emails, or empty string if none
     */
    public String getBccRecipients() {
        return recipients.stream()
            .filter(r -> "BCC".equals(r.getRecipientType()))
            .map(r -> r.getUser().getEmail())
            .collect(Collectors.joining(", "));
    }

    /**
     * Gets the first TO recipient's email for display in sent list.
     * 
     * <p>Used in the sent mail list to show a single recipient name
     * when space is limited.</p>
     * 
     * @return the first TO recipient's email, or "Recipients" if none
     */
    public String getFirstToRecipient() {
        return recipients.stream()
            .filter(r -> "TO".equals(r.getRecipientType()))
            .map(r -> r.getUser().getEmail())
            .findFirst()
            .orElse("Recipients");
    }
}

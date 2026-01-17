package com.wipro.iaf.email.mail.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.wipro.iaf.email.user.entity.User;

import lombok.Data;

/**
 * Entity representing a recipient of an email message.
 * 
 * <p>This junction entity links emails to their recipients and stores
 * recipient-specific state such as read status, starred status, deletion,
 * and snooze information. Uses a composite key of email ID and user ID.</p>
 * 
 * <p>Key features:
 * <ul>
 *   <li><b>Recipient Types</b> - TO, CC, or BCC classification</li>
 *   <li><b>Read Tracking</b> - Per-recipient read status</li>
 *   <li><b>Starring</b> - Recipients can star important emails</li>
 *   <li><b>Soft Delete</b> - Emails are soft-deleted per recipient</li>
 *   <li><b>Snooze</b> - Recipients can snooze emails until a later time</li>
 *   <li><b>Read Receipts</b> - Tracks if read receipt was sent to sender</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see Email
 * @see EmailRecipientId
 * @see User
 */
@Entity
@Table(name = "email_recipients", indexes = {
    @Index(name = "idx_recipient_user_deleted", columnList = "user_id, is_deleted"),
    @Index(name = "idx_recipient_type_deleted", columnList = "recipient_type, is_deleted"),
    @Index(name = "idx_recipient_deleted_at", columnList = "deleted_at")
})
@IdClass(EmailRecipientId.class)
@Data
public class EmailRecipient {

    /** The email this recipient record is for (part of composite key) */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Email email;

    /**
     * Checks if this email is starred by the recipient.
     * 
     * @return true if the recipient has starred this email
     */
    public boolean isStarred() {
        return starred != null && starred;
    }

    /**
     * Checks if read receipt notification was already sent for this recipient.
     * 
     * @return true if read receipt was sent to the sender
     */
    public boolean isReadReceiptSent() {
        return readReceiptSent != null && readReceiptSent;
    }

    /** The user who is receiving this email (part of composite key) */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    /** Type of recipient: "TO", "CC", or "BCC" */
    @Column(name = "recipient_type", length = 10)
    private String recipientType;

    /** Flag indicating if the recipient has read this email */
    @Column(name = "is_read")
    private boolean read;

    /** Flag indicating if the recipient has starred this email */
    @Column(name = "is_starred")
    private Boolean starred = false;

    /** Flag indicating if read receipt was sent to sender */
    @Column(name = "read_receipt_sent")
    private Boolean readReceiptSent = false;

    /** Flag indicating if recipient has deleted this email */
    @Column(name = "is_deleted")
    private boolean deleted;

    /** Timestamp when the email was deleted (for trash retention) */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /** Timestamp until which this email should be snoozed */
    @Column(name = "snoozed_until")
    private LocalDateTime snoozedUntil;

    /**
     * Checks if the email is currently snoozed (snooze time not yet reached).
     * 
     * @return true if email is snoozed and snooze time is in the future
     */
    public boolean isSnoozed() {
        return snoozedUntil != null && LocalDateTime.now().isBefore(snoozedUntil);
    }

    /**
     * Checks if a snoozed email has "woken up" (snooze time has passed).
     * 
     * @return true if email was snoozed but snooze time has expired
     */
    public boolean isSnoozeExpired() {
        return snoozedUntil != null && LocalDateTime.now().isAfter(snoozedUntil);
    }
}

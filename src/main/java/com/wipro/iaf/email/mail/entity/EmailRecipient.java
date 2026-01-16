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

@Entity
@Table(name = "email_recipients", indexes = {
    @Index(name = "idx_recipient_user_deleted", columnList = "user_id, is_deleted"),
    @Index(name = "idx_recipient_type_deleted", columnList = "recipient_type, is_deleted"),
    @Index(name = "idx_recipient_deleted_at", columnList = "deleted_at")
})
@IdClass(EmailRecipientId.class)
@Data

public class EmailRecipient {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Email email;

    public boolean isStarred() {
        return starred != null && starred;
    }

    public boolean isReadReceiptSent() {
        return readReceiptSent != null && readReceiptSent;
    }

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "recipient_type", length = 10)
    private String recipientType; // TO, CC, BCC

    @Column(name = "is_read")
    private boolean read;

    @Column(name = "is_starred")
    private Boolean starred = false;

    @Column(name = "read_receipt_sent")
    private Boolean readReceiptSent = false;

    @Column(name = "is_deleted")
    private boolean deleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "snoozed_until")
    private LocalDateTime snoozedUntil;

    /**
     * Check if the email is currently snoozed.
     */
    public boolean isSnoozed() {
        return snoozedUntil != null && LocalDateTime.now().isBefore(snoozedUntil);
    }

    /**
     * Check if a snoozed email has woken up (snooze expired).
     */
    public boolean isSnoozeExpired() {
        return snoozedUntil != null && LocalDateTime.now().isAfter(snoozedUntil);
    }

    
}

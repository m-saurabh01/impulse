package com.wipro.iaf.email.mail.entity;

import javax.persistence.*;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Junction entity linking emails to labels for organization.
 * 
 * <p>This entity implements the many-to-many relationship between emails
 * and labels, allowing emails to have multiple labels and labels to be
 * applied to multiple emails. The user ID is included in the key to
 * support label assignment per-recipient.</p>
 * 
 * <p>Uses a composite primary key of email ID, label ID, and user ID.</p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see Email
 * @see Label
 */
@Entity
@Table(name = "email_labels")
@IdClass(EmailLabel.EmailLabelId.class)
@Data
public class EmailLabel {

    /** The email this label is attached to */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_id")
    private Email email;

    /** The label attached to the email */
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "label_id")
    private Label label;

    /** The user who applied this label (supports per-recipient labeling) */
    @Id
    @Column(name = "user_id")
    private Long userId;

    /**
     * Default constructor required by JPA.
     */
    public EmailLabel() {}

    /**
     * Constructs a new EmailLabel linking an email to a label for a user.
     * 
     * @param email  the email to label
     * @param label  the label to apply
     * @param userId the ID of the user applying the label
     */
    public EmailLabel(Email email, Label label, Long userId) {
        this.email = email;
        this.label = label;
        this.userId = userId;
    }

    /**
     * Composite primary key class for EmailLabel.
     * 
     * <p>Defines the three-part key (email, label, user) for unique
     * identification of label assignments.</p>
     */
    @Data
    @EqualsAndHashCode
    public static class EmailLabelId implements Serializable {
        /** The email ID */
        private Long email;
        /** The label ID */
        private Long label;
        /** The user ID */
        private Long userId;
    }
}

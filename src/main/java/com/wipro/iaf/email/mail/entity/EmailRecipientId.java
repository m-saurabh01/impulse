package com.wipro.iaf.email.mail.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Composite primary key class for {@link EmailRecipient} entity.
 * 
 * <p>Defines the composite key consisting of email ID and user ID,
 * ensuring that each email-user combination is unique. This allows
 * tracking per-recipient state for each email.</p>
 * 
 * <p>Implements {@link Serializable} as required by JPA for composite keys.</p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see EmailRecipient
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRecipientId implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The email ID (references Email.id) */
    private Long email;

    /** The user ID (references User.id) */
    private Long user;
}


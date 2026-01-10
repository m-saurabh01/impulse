package com.wipro.iaf.email.mail.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.wipro.iaf.email.user.entity.User;

import lombok.Data;

@Entity
@Table(name = "email_recipients")
@IdClass(EmailRecipientId.class)
@Data

public class EmailRecipient {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Email email;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "recipient_type", length = 10)
    private String recipientType; // TO, CC, BCC

    @Column(name = "is_read")
    private boolean read;

    @Column(name = "is_deleted")
    private boolean deleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    
}

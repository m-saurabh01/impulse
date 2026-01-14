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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.wipro.iaf.email.user.entity.User;

import lombok.Data;

@Entity
@Table(name = "emails")
@Data
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User sender;

    @Column(length = 500)
    private String subject;

    @Lob
    @Column(name = "body_html")
    private String bodyHtml;

    @Column(name = "thread_id")
    private Long threadId;

    @Column(name = "is_draft")
    private boolean draft;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "email", fetch = FetchType.LAZY)
    private List<EmailRecipient> recipients = new ArrayList<>();

    /**
     * Get comma-separated list of TO recipient emails
     */
    public String getToRecipients() {
        return recipients.stream()
            .filter(r -> "TO".equals(r.getRecipientType()))
            .map(r -> r.getUser().getEmail())
            .collect(Collectors.joining(", "));
    }

    /**
     * Get comma-separated list of CC recipient emails
     */
    public String getCcRecipients() {
        return recipients.stream()
            .filter(r -> "CC".equals(r.getRecipientType()))
            .map(r -> r.getUser().getEmail())
            .collect(Collectors.joining(", "));
    }

    /**
     * Get comma-separated list of BCC recipient emails
     */
    public String getBccRecipients() {
        return recipients.stream()
            .filter(r -> "BCC".equals(r.getRecipientType()))
            .map(r -> r.getUser().getEmail())
            .collect(Collectors.joining(", "));
    }

    /**
     * Get first TO recipient email (for display in sent list)
     */
    public String getFirstToRecipient() {
        return recipients.stream()
            .filter(r -> "TO".equals(r.getRecipientType()))
            .map(r -> r.getUser().getEmail())
            .findFirst()
            .orElse("Recipients");
    }
}

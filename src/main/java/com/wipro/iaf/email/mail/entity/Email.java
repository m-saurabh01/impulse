package com.wipro.iaf.email.mail.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
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

    
}

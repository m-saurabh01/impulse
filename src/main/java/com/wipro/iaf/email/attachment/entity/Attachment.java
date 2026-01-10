package com.wipro.iaf.email.attachment.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.wipro.iaf.email.mail.entity.Email;

import lombok.Data;

@Entity
@Table(name = "attachments")
@Data
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Email email;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String storedFilename;

    @Column(nullable = false)
    private String mimeType;

    @Column(nullable = false)
    private long sizeBytes;

    private LocalDateTime createdAt = LocalDateTime.now();

    // getters/setters
}

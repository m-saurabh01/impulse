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

/**
 * Entity representing an email attachment.
 * 
 * <p>Stores metadata about files attached to emails. The actual file
 * content is stored on the filesystem, with the path referenced by
 * {@link #storedFilename}.</p>
 * 
 * <p>File storage structure: {base-path}/{emailId}/{storedFilename}</p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see Email
 */
@Entity
@Table(name = "attachments")
@Data
public class Attachment {

    /** Unique identifier for the attachment */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The email this attachment belongs to */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Email email;

    /** Original filename as uploaded by the user */
    @Column(nullable = false)
    private String originalFilename;

    /** UUID-based filename used for storage on disk */
    @Column(nullable = false)
    private String storedFilename;

    /** MIME type of the file (e.g., "application/pdf", "image/png") */
    @Column(nullable = false)
    private String mimeType;

    /** File size in bytes */
    @Column(nullable = false)
    private long sizeBytes;

    /** Timestamp when the attachment was created */
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Returns the file size in a human-readable format.
     * 
     * <p>Examples: "512 B", "1.5 KB", "2.3 MB", "1.0 GB"</p>
     * 
     * @return formatted size string
     */
    public String getFormattedSize() {
        if (sizeBytes < 1024) {
            return sizeBytes + " B";
        } else if (sizeBytes < 1024 * 1024) {
            return String.format("%.1f KB", sizeBytes / 1024.0);
        } else if (sizeBytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", sizeBytes / (1024.0 * 1024));
        } else {
            return String.format("%.1f GB", sizeBytes / (1024.0 * 1024 * 1024));
        }
    }

    // getters/setters
}

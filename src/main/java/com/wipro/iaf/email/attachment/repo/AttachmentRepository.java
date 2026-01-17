package com.wipro.iaf.email.attachment.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wipro.iaf.email.attachment.entity.Attachment;

/**
 * Repository interface for Attachment entity database operations.
 * 
 * <p>Provides CRUD operations and custom queries for managing
 * email attachment metadata.</p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see Attachment
 */
public interface AttachmentRepository
        extends JpaRepository<Attachment, Long> {

    /**
     * Finds all attachments for a specific email.
     * 
     * @param emailId the email ID
     * @return list of attachments for the email
     */
    List<Attachment> findByEmailId(Long emailId);

    /**
     * Deletes all attachments for a specific email.
     * 
     * <p>Note: This only removes database records. Physical files
     * must be deleted separately.</p>
     * 
     * @param emailId the email ID
     */
    void deleteByEmailId(Long emailId);
}

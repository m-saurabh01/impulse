package com.wipro.iaf.email.attachment.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.wipro.iaf.email.attachment.entity.Attachment;
import com.wipro.iaf.email.attachment.repo.AttachmentRepository;
import com.wipro.iaf.email.mail.entity.Email;

/**
 * Service for managing email attachments.
 * 
 * <p>Handles saving, retrieving, and copying attachments. Files are
 * stored on the filesystem with UUID-based names to prevent conflicts
 * and security issues.</p>
 * 
 * <p>Storage structure: {base-path}/{emailId}/{uuid}</p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see Attachment
 * @see AttachmentRepository
 */
@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepo;
    private final Path basePath;

    /**
     * Constructs the AttachmentService with required dependencies.
     * 
     * @param attachmentRepo repository for attachment operations
     * @param baseDir        the base directory for attachment storage
     */
    public AttachmentService(
            AttachmentRepository attachmentRepo,
            @Value("${mail.attachments.base-path}") String baseDir) {

        this.attachmentRepo = attachmentRepo;
        this.basePath = Paths.get(baseDir);
    }

    /**
     * Saves attachments for an email.
     * 
     * <p>Creates a directory for the email if needed, saves each file
     * with a UUID-based name, and creates database records.</p>
     * 
     * @param email the email to attach files to
     * @param files list of multipart files to save
     * @throws IOException if file writing fails
     */
    @Transactional
    public void saveAttachments(Email email, List<MultipartFile> files)
            throws IOException {

        if (files == null || files.isEmpty()) return;

        Path emailDir = basePath.resolve(String.valueOf(email.getId()));
        Files.createDirectories(emailDir);

        for (MultipartFile file : files) {

        	if (file == null || file.isEmpty()) {
                continue;
            }

            String storedName = UUID.randomUUID().toString();
            Path target = emailDir.resolve(storedName);

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }

            Attachment a = new Attachment();
            a.setEmail(email);
            a.setOriginalFilename(file.getOriginalFilename());
            a.setStoredFilename(storedName);
            a.setMimeType(file.getContentType());
            a.setSizeBytes(file.getSize());

            attachmentRepo.save(a);
        }
    }

    /**
     * Finds all attachments for an email.
     * 
     * @param id the email ID
     * @return list of attachments for the email
     */
	public List<Attachment> findByEmailId(Long id) {
		return attachmentRepo.findByEmailId(id);
	}

    /**
     * Copies attachments from source email to target email.
     * 
     * <p>Used when forwarding emails to include original attachments.
     * Creates new file copies and database records.</p>
     * 
     * @param targetEmail         the email to copy attachments to
     * @param sourceAttachmentIds list of attachment IDs to copy
     * @throws IOException if file copying fails
     */
    @Transactional
    public void copyAttachments(Email targetEmail, List<Long> sourceAttachmentIds) throws IOException {
        if (sourceAttachmentIds == null || sourceAttachmentIds.isEmpty()) return;

        Path targetDir = basePath.resolve(String.valueOf(targetEmail.getId()));
        Files.createDirectories(targetDir);

        for (Long attachmentId : sourceAttachmentIds) {
            Attachment source = attachmentRepo.findById(attachmentId).orElse(null);
            if (source == null) continue;

            // Copy the file
            Path sourceFile = basePath.resolve(String.valueOf(source.getEmail().getId()))
                    .resolve(source.getStoredFilename());
            
            if (!Files.exists(sourceFile)) continue;

            String newStoredName = UUID.randomUUID().toString();
            Path targetFile = targetDir.resolve(newStoredName);
            Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);

            // Create new attachment record
            Attachment newAttachment = new Attachment();
            newAttachment.setEmail(targetEmail);
            newAttachment.setOriginalFilename(source.getOriginalFilename());
            newAttachment.setStoredFilename(newStoredName);
            newAttachment.setMimeType(source.getMimeType());
            newAttachment.setSizeBytes(source.getSizeBytes());

            attachmentRepo.save(newAttachment);
        }
    }}
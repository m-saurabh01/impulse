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

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepo;
    private final Path basePath;

    public AttachmentService(
            AttachmentRepository attachmentRepo,
            @Value("${mail.attachments.base-path}") String baseDir) {

        this.attachmentRepo = attachmentRepo;
        this.basePath = Paths.get(baseDir);
    }

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

	public List<Attachment> findByEmailId(Long id) {
		return attachmentRepo.findByEmailId(id);
	}

    /**
     * Copy attachments from original email to new email (for forwarding)
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
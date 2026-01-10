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

  
}

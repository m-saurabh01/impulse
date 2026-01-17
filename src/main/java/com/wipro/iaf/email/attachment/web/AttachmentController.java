package com.wipro.iaf.email.attachment.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wipro.iaf.email.attachment.entity.Attachment;
import com.wipro.iaf.email.attachment.repo.AttachmentRepository;
import com.wipro.iaf.email.mail.repo.EmailRecipientRepository;
import com.wipro.iaf.email.security.SecurityUser;

/**
 * Controller for file attachment download operations.
 * 
 * <p>Handles secure file downloads for email attachments.
 * Verifies that the requesting user is either a recipient or
 * the sender of the email before allowing download.</p>
 * 
 * <p>Files are stored on the filesystem in a directory structure
 * of {base-path}/{emailId}/{storedFilename}.</p>
 * 
 * <p>Endpoints:
 * <ul>
 *   <li>{@code GET /attachments/{id}} - Download an attachment by ID</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see Attachment
 * @see AttachmentRepository
 */
@Controller
@RequestMapping("/attachments")
public class AttachmentController {

    private final AttachmentRepository attachmentRepo;
    private final EmailRecipientRepository recipientRepo;
    private final Path basePath;

    /**
     * Constructs the AttachmentController with required dependencies.
     * 
     * @param attachmentRepo repository for attachment operations
     * @param recipientRepo  repository for email recipient checks
     * @param baseDir        the base directory for attachment storage
     */
    public AttachmentController(
            AttachmentRepository attachmentRepo,
            EmailRecipientRepository recipientRepo,
            @Value("${mail.attachments.base-path}") String baseDir) {

        this.attachmentRepo = attachmentRepo;
        this.recipientRepo = recipientRepo;
        this.basePath = Paths.get(baseDir);
    }

    /**
     * Downloads an attachment by ID.
     * 
     * <p>Verifies that the user is authorized to access the attachment
     * by checking if they are a recipient or sender of the email.
     * Returns 403 Forbidden if not authorized.</p>
     * 
     * @param id       the attachment ID
     * @param user     the authenticated user
     * @param response the HTTP response for writing the file
     * @throws IOException          if file read fails
     * @throws IllegalArgumentException if attachment not found
     */
    @GetMapping("/{id}")
    public void download(@PathVariable Long id,
                         @AuthenticationPrincipal SecurityUser user,
                         HttpServletResponse response) throws IOException {

        Attachment a = attachmentRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Not found"));

        boolean allowed =
            recipientRepo.existsByEmailIdAndUserId(
                a.getEmail().getId(), user.getId())
            || a.getEmail().getSender().getId().equals(user.getId());

        if (!allowed) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Path file = basePath
            .resolve(String.valueOf(a.getEmail().getId()))
            .resolve(a.getStoredFilename());

        response.setContentType(a.getMimeType());
        response.setHeader(
            "Content-Disposition",
            "attachment; filename=\"" + a.getOriginalFilename() + "\"");

        try (InputStream in = Files.newInputStream(file);
             OutputStream out = response.getOutputStream()) {

            IOUtils.copy(in, out);
        }
    }
}

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

@Controller
@RequestMapping("/attachments")
public class AttachmentController {

    private final AttachmentRepository attachmentRepo;
    private final EmailRecipientRepository recipientRepo;
    private final Path basePath;

    public AttachmentController(
            AttachmentRepository attachmentRepo,
            EmailRecipientRepository recipientRepo,
            @Value("${mail.attachments.base-path}") String baseDir) {

        this.attachmentRepo = attachmentRepo;
        this.recipientRepo = recipientRepo;
        this.basePath = Paths.get(baseDir);
    }

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

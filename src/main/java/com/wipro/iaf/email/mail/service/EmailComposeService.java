package com.wipro.iaf.email.mail.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wipro.iaf.email.attachment.service.AttachmentService;
import com.wipro.iaf.email.mail.dto.ComposeEmailRequest;
import com.wipro.iaf.email.mail.entity.Email;
import com.wipro.iaf.email.mail.entity.EmailRecipient;
import com.wipro.iaf.email.mail.repo.EmailRecipientRepository;
import com.wipro.iaf.email.mail.repo.EmailRepository;
import com.wipro.iaf.email.security.SecurityUser;
import com.wipro.iaf.email.user.entity.User;
import com.wipro.iaf.email.user.repo.UserRepository;

@Service
public class EmailComposeService {

	private final EmailRepository emailRepo;
    private final EmailRecipientRepository recipientRepo;
    private final UserRepository userRepo;
    private final AttachmentService attachmentService;

    public EmailComposeService(
            EmailRepository emailRepo,
            EmailRecipientRepository recipientRepo,
            UserRepository userRepo,
            AttachmentService attachmentService) {

        this.emailRepo = emailRepo;
        this.recipientRepo = recipientRepo;
        this.userRepo = userRepo;
        this.attachmentService = attachmentService;
    }

    @Transactional
    public void composeAndSend(ComposeEmailRequest req, SecurityUser sender) {

        Email email = new Email();
        email.setSender(userRepo.getById(sender.getId()));
        email.setSubject(req.getSubject());
        email.setBodyHtml(req.getBodyHtml());
        email.setDraft(req.isDraft());

        emailRepo.save(email); // ID generated here

        // Thread handling
        if (req.getThreadId() != null) {
            email.setThreadId(req.getThreadId());
        } else {
            email.setThreadId(email.getId());
        }

        emailRepo.save(email);

        // SAVE ATTACHMENTS HERE (IMPORTANT)
        try {
            attachmentService.saveAttachments(email, req.getAttachments());
        } catch (IOException ex) {
            throw new RuntimeException("Attachment upload failed", ex);
        }

        // Drafts stop here
        if (req.isDraft()) {
            return;
        }

        addRecipients(email, req.getTo(), "TO");
        addRecipients(email, req.getCc(), "CC");
        addRecipients(email, req.getBcc(), "BCC");
    }


    private void addRecipients(Email email,
                               List<String> addresses,
                               String type) {

        if (addresses == null) return;

        for (String addr : addresses) {
            User user = userRepo.findByEmail(addr)
                .orElseThrow(() ->
                    new IllegalArgumentException("Unknown user: " + addr));

            EmailRecipient r = new EmailRecipient();
            r.setEmail(email);
            r.setUser(user);
            r.setRecipientType(type);
            r.setRead(false);
            r.setDeleted(false);

            recipientRepo.save(r);
        }
    }
}

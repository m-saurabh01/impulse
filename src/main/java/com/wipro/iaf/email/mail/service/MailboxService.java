package com.wipro.iaf.email.mail.service;



import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wipro.iaf.email.mail.entity.Email;
import com.wipro.iaf.email.mail.entity.EmailRecipient;
import com.wipro.iaf.email.mail.entity.EmailRecipientId;
import com.wipro.iaf.email.mail.repo.EmailRecipientRepository;
import com.wipro.iaf.email.mail.repo.EmailRepository;

@Service
public class MailboxService {

    private final EmailRecipientRepository recipientRepo;
    private final EmailRepository emailRepo;

    public MailboxService(EmailRecipientRepository recipientRepo,
                          EmailRepository emailRepo) {
        this.recipientRepo = recipientRepo;
        this.emailRepo = emailRepo;
    }

    @Transactional(readOnly = true)
    public Page<EmailRecipient> inbox(Long userId, Pageable pageable) {
        return recipientRepo.findInbox(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Email> sent(Long userId, Pageable pageable) {
        return emailRepo.findBySenderIdAndDraftFalseOrderByCreatedAtDesc(
                userId, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<Email> drafts(Long userId, Pageable pageable) {
        return emailRepo
            .findBySenderIdAndDraftTrueOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<EmailRecipient> trash(Long userId, Pageable pageable) {
        return recipientRepo.findTrash(userId, pageable);
    }

    @Transactional
    public void moveToTrash(EmailRecipientId id) {
        EmailRecipient r = recipientRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Mail not found"));

        r.setDeleted(true);
        r.setDeletedAt(LocalDateTime.now());
    }

    @Transactional
    public void restoreFromTrash(EmailRecipientId id) {
        EmailRecipient r = recipientRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Mail not found"));

        r.setDeleted(false);
        r.setDeletedAt(null);
    }
    
    @Transactional(readOnly = true)
    public Email findEmailForUser(Long emailId, Long userId) throws AccessDeniedException {

        // sender can preview
        Optional<Email> sent = emailRepo.findByIdAndSenderId(emailId, userId);
        if (sent.isPresent()) {
            return sent.get();
        }

        // recipient can preview
        return recipientRepo.findByEmailIdAndUserId(emailId, userId)
            .map(EmailRecipient::getEmail)
            .orElseThrow(() ->
                new AccessDeniedException("No access to email"));
    }

}

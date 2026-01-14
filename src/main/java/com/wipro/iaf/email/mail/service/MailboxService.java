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
    public Page<EmailRecipient> sent(Long userId, Pageable pageable) {
        return recipientRepo.findSent(userId, pageable);
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

        // Check if user has access via EmailRecipient (as sender, recipient, etc.)
        return recipientRepo.findByEmailIdAndUserId(emailId, userId)
            .map(EmailRecipient::getEmail)
            .orElseThrow(() ->
                new AccessDeniedException("No access to email"));
    }

    @Transactional
    public void markAsRead(Long emailId, Long userId) {
        recipientRepo.findByEmailIdAndUserId(emailId, userId)
            .ifPresent(r -> r.setRead(true));
    }

    /**
     * Move an email to trash (works for both sent and received)
     * Uses the unified EmailRecipient approach
     */
    @Transactional
    public void moveToTrash(Long emailId, Long userId) {
        recipientRepo.findByEmailIdAndUserId(emailId, userId)
            .ifPresent(r -> {
                r.setDeleted(true);
                r.setDeletedAt(LocalDateTime.now());
            });
    }

    /**
     * Restore an email from trash (works for both sent and received)
     */
    @Transactional
    public void restoreFromTrash(Long emailId, Long userId) {
        recipientRepo.findByEmailIdAndUserId(emailId, userId)
            .ifPresent(r -> {
                r.setDeleted(false);
                r.setDeletedAt(null);
            });
    }

    /**
     * Permanently delete an email (remove the EmailRecipient record)
     */
    @Transactional
    public void permanentDelete(Long emailId, Long userId) {
        recipientRepo.findByEmailIdAndUserId(emailId, userId)
            .ifPresent(r -> recipientRepo.delete(r));
    }

    /**
     * Empty all trash for a user (permanently delete all trashed emails)
     */
    @Transactional
    public void emptyTrash(Long userId) {
        recipientRepo.deleteAllTrashedByUserId(userId);
    }

}

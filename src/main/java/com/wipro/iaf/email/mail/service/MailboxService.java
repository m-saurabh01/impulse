package com.wipro.iaf.email.mail.service;



import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Page<EmailRecipient> searchInbox(Long userId, String query, Pageable pageable) {
        return recipientRepo.searchInbox(userId, query, pageable);
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

    /**
     * Get conversation thread for an email (excluding the current email)
     * Returns all emails in the same thread, ordered by creation date
     */
    @Transactional(readOnly = true)
    public List<Email> getConversationThread(Long threadId, Long currentEmailId) {
        List<Email> threadEmails = emailRepo.findByThreadId(threadId);
        // Exclude the current email from the thread list
        return threadEmails.stream()
            .filter(e -> !e.getId().equals(currentEmailId))
            .collect(Collectors.toList());
    }

    /**
     * Delete a draft permanently (only drafts owned by the user)
     */
    @Transactional
    public void deleteDraft(Long emailId, Long userId) {
        emailRepo.findByIdAndSenderId(emailId, userId)
            .filter(Email::isDraft)
            .ifPresent(email -> {
                // Delete recipients first
                recipientRepo.deleteByEmailId(emailId);
                // Delete the email
                emailRepo.delete(email);
            });
    }

    /**
     * Get starred emails for a user
     */
    @Transactional(readOnly = true)
    public Page<EmailRecipient> starred(Long userId, Pageable pageable) {
        return recipientRepo.findStarred(userId, pageable);
    }

    /**
     * Toggle star status for an email
     */
    @Transactional
    public boolean toggleStar(Long emailId, Long userId) {
        Optional<EmailRecipient> recipientOpt = recipientRepo.findByEmailIdAndUserId(emailId, userId);
        if (recipientOpt.isPresent()) {
            EmailRecipient r = recipientOpt.get();
            boolean newStarred = !r.isStarred();
            r.setStarred(newStarred);
            return newStarred;
        }
        return false;
    }

    /**
     * Mark email as read and handle read receipt if requested
     */
    @Transactional
    public void markAsReadWithReceipt(Long emailId, Long userId) {
        recipientRepo.findByEmailIdAndUserId(emailId, userId)
            .ifPresent(r -> {
                r.setRead(true);
                // Check if read receipt is requested and not already sent
                if (r.getEmail().isReadReceiptRequested() && !r.isReadReceiptSent()) {
                    r.setReadReceiptSent(true);
                    // TODO: Send notification to sender about read receipt
                }
            });
    }

    /**
     * Get email recipient for a user
     */
    @Transactional(readOnly = true)
    public Optional<EmailRecipient> getRecipient(Long emailId, Long userId) {
        return recipientRepo.findByEmailIdAndUserId(emailId, userId);
    }

    /**
     * Count unread emails in inbox for a user
     */
    @Transactional(readOnly = true)
    public long countUnreadInbox(Long userId) {
        return recipientRepo.countUnreadInbox(userId);
    }

}

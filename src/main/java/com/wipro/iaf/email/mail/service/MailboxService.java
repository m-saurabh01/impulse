package com.wipro.iaf.email.mail.service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wipro.iaf.email.mail.dto.ReadReceiptNotification;
import com.wipro.iaf.email.mail.entity.Email;
import com.wipro.iaf.email.mail.entity.EmailRecipient;
import com.wipro.iaf.email.mail.entity.EmailRecipientId;
import com.wipro.iaf.email.mail.repo.EmailRecipientRepository;
import com.wipro.iaf.email.mail.repo.EmailRepository;

/**
 * Service class for managing user mailbox operations in the PulseMail application.
 * <p>
 * This service provides comprehensive mailbox functionality including:
 * <ul>
 *   <li>Retrieving emails from inbox, sent, drafts, trash, starred, and snoozed folders</li>
 *   <li>Moving emails to/from trash and permanent deletion</li>
 *   <li>Marking emails as read with optional read receipt handling</li>
 *   <li>Email starring and snooze functionality</li>
 *   <li>Conversation thread management</li>
 *   <li>Inbox search and unread count tracking</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-16
 * @see Email
 * @see EmailRecipient
 * @see NotificationService
 */
@Service
public class MailboxService {

    private final EmailRecipientRepository recipientRepo;
    private final EmailRepository emailRepo;
    private final NotificationService notificationService;

    /**
     * Constructs a new MailboxService with required dependencies.
     * 
     * @param recipientRepo repository for email recipient operations
     * @param emailRepo repository for email CRUD operations
     * @param notificationService service for sending real-time notifications
     */
    public MailboxService(EmailRecipientRepository recipientRepo,
                          EmailRepository emailRepo,
                          NotificationService notificationService) {
        this.recipientRepo = recipientRepo;
        this.emailRepo = emailRepo;
        this.notificationService = notificationService;
    }

    /**
     * Retrieves a paginated list of inbox emails for a user.
     * <p>
     * Returns received emails that are not deleted or in trash.
     * </p>
     * 
     * @param userId the ID of the user whose inbox to retrieve
     * @param pageable pagination and sorting parameters
     * @return a page of EmailRecipient records representing inbox emails
     */
    @Transactional(readOnly = true)
    public Page<EmailRecipient> inbox(Long userId, Pageable pageable) {
        return recipientRepo.findInbox(userId, pageable);
    }

    /**
     * Searches the inbox for emails matching the specified query.
     * 
     * @param userId the ID of the user whose inbox to search
     * @param query the search query string
     * @param pageable pagination and sorting parameters
     * @return a page of EmailRecipient records matching the search criteria
     */
    @Transactional(readOnly = true)
    public Page<EmailRecipient> searchInbox(Long userId, String query, Pageable pageable) {
        return recipientRepo.searchInbox(userId, query, pageable);
    }

    /**
     * Retrieves a paginated list of sent emails for a user.
     * 
     * @param userId the ID of the user whose sent folder to retrieve
     * @param pageable pagination and sorting parameters
     * @return a page of EmailRecipient records representing sent emails
     */
    @Transactional(readOnly = true)
    public Page<EmailRecipient> sent(Long userId, Pageable pageable) {
        return recipientRepo.findSent(userId, pageable);
    }

    /**
     * Retrieves a paginated list of draft emails for a user.
     * 
     * @param userId the ID of the user whose drafts to retrieve
     * @param pageable pagination and sorting parameters
     * @return a page of Email records that are marked as drafts
     */
    @Transactional(readOnly = true)
    public Page<Email> drafts(Long userId, Pageable pageable) {
        return emailRepo
            .findBySenderIdAndDraftTrueOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Retrieves a paginated list of trashed emails for a user.
     * 
     * @param userId the ID of the user whose trash folder to retrieve
     * @param pageable pagination and sorting parameters
     * @return a page of EmailRecipient records that are in trash
     */
    @Transactional(readOnly = true)
    public Page<EmailRecipient> trash(Long userId, Pageable pageable) {
        return recipientRepo.findTrash(userId, pageable);
    }

    /**
     * Moves an email to trash using the composite EmailRecipientId.
     * 
     * @param id the composite ID of the email recipient record
     * @throws IllegalArgumentException if the mail is not found
     */
    @Transactional
    public void moveToTrash(EmailRecipientId id) {
        EmailRecipient r = recipientRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Mail not found"));

        r.setDeleted(true);
        r.setDeletedAt(LocalDateTime.now());
    }

    /**
     * Restores an email from trash using the composite EmailRecipientId.
     * 
     * @param id the composite ID of the email recipient record
     * @throws IllegalArgumentException if the mail is not found
     */
    @Transactional
    public void restoreFromTrash(EmailRecipientId id) {
        EmailRecipient r = recipientRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Mail not found"));

        r.setDeleted(false);
        r.setDeletedAt(null);
    }

    /**
     * Finds and returns an email if the specified user has access to it.
     * <p>
     * Access is determined by checking if the user has an EmailRecipient
     * record for the email (as sender, TO, CC, or BCC recipient).
     * </p>
     * 
     * @param emailId the ID of the email to retrieve
     * @param userId the ID of the user requesting access
     * @return the Email if the user has access
     * @throws AccessDeniedException if the user does not have access to the email
     */
    @Transactional(readOnly = true)
    public Email findEmailForUser(Long emailId, Long userId) throws AccessDeniedException {

        // Check if user has access via EmailRecipient (as sender, recipient, etc.)
        return recipientRepo.findByEmailIdAndUserId(emailId, userId)
            .map(EmailRecipient::getEmail)
            .orElseThrow(() ->
                new AccessDeniedException("No access to email"));
    }

    /**
     * Marks an email as read for the specified user.
     * 
     * @param emailId the ID of the email to mark as read
     * @param userId the ID of the user who read the email
     */
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
                    recipientRepo.save(r);
                    
                    // Send WebSocket notification to the original sender
                    Email email = r.getEmail();
                    String senderEmail = email.getSender().getEmail();
                    String readerEmail = r.getUser().getEmail();
                    
                    ReadReceiptNotification notification = new ReadReceiptNotification(
                        emailId,
                        readerEmail,
                        email.getSubject() != null ? email.getSubject() : "(No subject)",
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a"))
                    );
                    
                    notificationService.notifyReadReceipt(senderEmail, notification);
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

    /**
     * Snooze an email until a specific time
     */
    @Transactional
    public void snoozeEmail(Long emailId, Long userId, LocalDateTime snoozedUntil) {
        recipientRepo.updateSnooze(emailId, userId, snoozedUntil);
    }

    /**
     * Unsnooze an email (clear the snooze time)
     */
    @Transactional
    public void unsnoozeEmail(Long emailId, Long userId) {
        recipientRepo.updateSnooze(emailId, userId, null);
    }

    /**
     * Get snoozed emails for a user
     */
    @Transactional(readOnly = true)
    public Page<EmailRecipient> snoozed(Long userId, Pageable pageable) {
        return recipientRepo.findSnoozed(userId, LocalDateTime.now(), pageable);
    }

    /**
     * Count snoozed emails for a user
     */
    @Transactional(readOnly = true)
    public long countSnoozed(Long userId) {
        return recipientRepo.countSnoozed(userId, LocalDateTime.now());
    }

}

package com.wipro.iaf.email.mail.service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wipro.iaf.email.attachment.service.AttachmentService;
import com.wipro.iaf.email.mail.dto.ComposeEmailRequest;
import com.wipro.iaf.email.mail.dto.EmailNotification;
import com.wipro.iaf.email.mail.entity.Email;
import com.wipro.iaf.email.mail.entity.EmailRecipient;
import com.wipro.iaf.email.mail.repo.EmailRecipientRepository;
import com.wipro.iaf.email.mail.repo.EmailRepository;
import com.wipro.iaf.email.security.SecurityUser;
import com.wipro.iaf.email.user.entity.User;
import com.wipro.iaf.email.user.repo.UserRepository;
import com.wipro.iaf.email.user.service.AchievementService;

/**
 * Service class responsible for composing and sending emails in the PulseMail application.
 * <p>
 * This service handles all email composition operations including:
 * <ul>
 *   <li>Validating recipient email addresses and checking if users exist in the system</li>
 *   <li>Creating new emails and updating existing drafts</li>
 *   <li>Managing email recipients (TO, CC, BCC)</li>
 *   <li>Handling email attachments</li>
 *   <li>Sending real-time WebSocket notifications to recipients</li>
 *   <li>Triggering achievement checks after sending emails</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-16
 * @see Email
 * @see EmailRecipient
 * @see ComposeEmailRequest
 * @see NotificationService
 */
@Service
public class EmailComposeService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

	private final EmailRepository emailRepo;
    private final EmailRecipientRepository recipientRepo;
    private final UserRepository userRepo;
    private final AttachmentService attachmentService;
    private final NotificationService notificationService;
    private final AchievementService achievementService;

    /**
     * Constructs a new EmailComposeService with all required dependencies.
     * 
     * @param emailRepo repository for email CRUD operations
     * @param recipientRepo repository for managing email recipients
     * @param userRepo repository for user lookups
     * @param attachmentService service for handling email attachments
     * @param notificationService service for sending real-time notifications
     * @param achievementService service for tracking user achievements
     */
    public EmailComposeService(
            EmailRepository emailRepo,
            EmailRecipientRepository recipientRepo,
            UserRepository userRepo,
            AttachmentService attachmentService,
            NotificationService notificationService,
            AchievementService achievementService) {

        this.emailRepo = emailRepo;
        this.recipientRepo = recipientRepo;
        this.userRepo = userRepo;
        this.attachmentService = attachmentService;
        this.notificationService = notificationService;
        this.achievementService = achievementService;
    }

    /**
     * Validates and sends an email with comprehensive error handling.
     * <p>
     * This method performs the following validations before sending:
     * <ul>
     *   <li>Ensures at least one recipient is specified (unless saving as draft)</li>
     *   <li>Validates email format using regex pattern matching</li>
     *   <li>Verifies that all recipients exist as registered users in the system</li>
     * </ul>
     * </p>
     * 
     * @param req the compose email request containing all email details
     * @param sender the authenticated user sending the email
     * @return EmailComposeResult containing success status and any validation errors
     * @throws RuntimeException if an unexpected error occurs during email processing
     */
    @Transactional
    public EmailComposeResult composeAndSendWithValidation(ComposeEmailRequest req, SecurityUser sender) {
        List<String> errors = new ArrayList<>();
        List<String> invalidEmails = new ArrayList<>();
        List<String> unknownUsers = new ArrayList<>();

        // Collect all recipient emails
        List<String> allToValidate = new ArrayList<>();
        if (req.getTo() != null) allToValidate.addAll(req.getTo());
        if (req.getCc() != null) allToValidate.addAll(req.getCc());
        if (req.getBcc() != null) allToValidate.addAll(req.getBcc());

        // Must have at least one recipient (unless saving as draft)
        if (!req.isDraft() && allToValidate.isEmpty()) {
            errors.add("Please specify at least one recipient");
            return new EmailComposeResult(false, errors);
        }

        // Validate each email
        for (String email : allToValidate) {
            if (email == null || email.trim().isEmpty()) continue;
            
            String trimmed = email.trim().toLowerCase();
            
            // Check email format
            if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
                invalidEmails.add(email);
                continue;
            }
            
            // Check if user exists in system and is not deleted
            java.util.Optional<User> recipientUser = userRepo.findByEmail(trimmed);
            if (!recipientUser.isPresent()) {
                unknownUsers.add(email);
            } else if (recipientUser.get().isDeleted()) {
                unknownUsers.add(email + " (account deleted)");
            }
        }

        if (!invalidEmails.isEmpty()) {
            errors.add("Invalid email format: " + String.join(", ", invalidEmails));
        }
        if (!unknownUsers.isEmpty()) {
            errors.add("Users not found: " + String.join(", ", unknownUsers));
        }

        if (!errors.isEmpty()) {
            return new EmailComposeResult(false, errors);
        }

        // All validations passed, proceed with sending
        try {
            composeAndSend(req, sender);
            return new EmailComposeResult(true, null);
        } catch (Exception e) {
            errors.add("Failed to send email: " + e.getMessage());
            return new EmailComposeResult(false, errors);
        }
    }

    /**
     * Composes and sends an email or saves it as a draft.
     * <p>
     * This method handles the complete email composition workflow:
     * <ul>
     *   <li>Creates new emails or updates existing drafts</li>
     *   <li>Sets email properties (subject, body, read receipt)</li>
     *   <li>Manages email threading for conversations</li>
     *   <li>Saves attachments and copies forwarded attachments</li>
     *   <li>Creates recipient records for all addressees</li>
     *   <li>Sends WebSocket notifications to all recipients</li>
     *   <li>Triggers achievement checks for the sender</li>
     * </ul>
     * </p>
     * 
     * @param req the compose email request containing all email details
     * @param sender the authenticated user composing the email
     * @throws IllegalArgumentException if draft ID is provided but not found for the sender
     * @throws RuntimeException if attachment upload fails
     */
    @Transactional
    public void composeAndSend(ComposeEmailRequest req, SecurityUser sender) {

        Email email;
        
        // Check if updating existing draft
        if (req.getDraftId() != null) {
            email = emailRepo.findByIdAndSenderId(req.getDraftId(), sender.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Draft not found"));
            // Clear existing recipients when updating (for both draft and send)
            recipientRepo.deleteByEmailId(email.getId());
        } else {
            email = new Email();
            email.setSender(userRepo.getById(sender.getId()));
        }
        
        email.setSubject(req.getSubject());
        email.setBodyHtml(req.getBodyHtml());
        email.setDraft(req.isDraft());
        email.setReadReceiptRequested(req.isReadReceiptRequested());

        emailRepo.save(email); // ID generated here for new emails

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
            // Copy forwarded attachments if present
            attachmentService.copyAttachments(email, req.getForwardAttachmentIds());
        } catch (IOException ex) {
            throw new RuntimeException("Attachment upload failed", ex);
        }

        // Save recipients for both drafts and sent emails
        // (For drafts, this allows us to restore recipients when editing)
        addRecipients(email, req.getTo(), "TO");
        addRecipients(email, req.getCc(), "CC");
        addRecipients(email, req.getBcc(), "BCC");

        // Drafts stop here (don't create SENDER record or send notifications)
        if (req.isDraft()) {
            return;
        }

        // Create SENDER recipient record for the sender's "Sent" folder
        User senderUser = userRepo.getById(sender.getId());
        EmailRecipient senderRecipient = new EmailRecipient();
        senderRecipient.setEmail(email);
        senderRecipient.setUser(senderUser);
        senderRecipient.setRecipientType("SENDER");
        senderRecipient.setRead(true); // Sender has obviously read their own email
        senderRecipient.setDeleted(false);
        recipientRepo.save(senderRecipient);

        // Collect all recipients for notification
        List<String> allRecipients = new ArrayList<>();
        if (req.getTo() != null) allRecipients.addAll(req.getTo());
        if (req.getCc() != null) allRecipients.addAll(req.getCc());
        if (req.getBcc() != null) allRecipients.addAll(req.getBcc());

        // Send real-time notifications to all recipients
        sendNotifications(email, senderUser.getEmail(), allRecipients);
        
        // Check for achievements asynchronously (doesn't block response)
        achievementService.checkAndAwardAchievements(sender.getId());
    }

    /**
     * Sends real-time WebSocket notifications to all email recipients.
     * <p>
     * Creates a notification preview by stripping HTML tags from the email body
     * and truncating to 100 characters. Formats the timestamp for display.
     * </p>
     * 
     * @param email the email being sent
     * @param senderEmail the email address of the sender
     * @param recipients list of recipient email addresses to notify
     */
    private void sendNotifications(Email email, String senderEmail, List<String> recipients) {
        // Create preview text from body (strip HTML, limit length)
        String preview = email.getBodyHtml() != null 
            ? email.getBodyHtml().replaceAll("<[^>]*>", "").trim()
            : "";
        if (preview.length() > 100) {
            preview = preview.substring(0, 100) + "...";
        }

        // Format date like inbox.jsp: "Jan 14"
        String timestamp = email.getCreatedAt() != null 
            ? email.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd"))
            : "";

        EmailNotification notification = new EmailNotification(
            email.getId(),
            senderEmail,
            email.getSubject() != null ? email.getSubject() : "(No subject)",
            preview,
            timestamp
        );

        notificationService.notifyNewEmailToAll(recipients, notification);
    }

    /**
     * Adds recipients of a specific type to an email.
     * <p>
     * Creates EmailRecipient records for each provided email address.
     * Recipients are marked as unread and not deleted by default.
     * </p>
     * 
     * @param email the email to add recipients to
     * @param addresses list of recipient email addresses
     * @param type the recipient type ("TO", "CC", or "BCC")
     * @throws IllegalArgumentException if any email address does not correspond to a registered user
     */
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

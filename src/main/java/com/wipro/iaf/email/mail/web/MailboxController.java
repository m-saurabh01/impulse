package com.wipro.iaf.email.mail.web;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wipro.iaf.email.attachment.entity.Attachment;
import com.wipro.iaf.email.attachment.service.AttachmentService;
import com.wipro.iaf.email.mail.entity.Email;
import com.wipro.iaf.email.mail.service.MailboxService;
import com.wipro.iaf.email.security.SecurityUser;

/**
 * Controller for managing email mailbox operations.
 * 
 * <p>Handles all mailbox-related HTTP endpoints including inbox viewing,
 * email preview, trash management, starring, and bulk operations.
 * All endpoints require authentication.</p>
 * 
 * <p>Endpoints:
 * <ul>
 *   <li>{@code GET /mail/inbox} - View inbox with optional search</li>
 *   <li>{@code GET /mail/preview} - View email details with attachments</li>
 *   <li>{@code POST /mail/markAsRead} - Mark email as read</li>
 *   <li>{@code POST /mail/moveToTrash} - Move email to trash</li>
 *   <li>{@code POST /mail/restoreFromTrash} - Restore from trash</li>
 *   <li>{@code POST /mail/permanentDelete} - Permanently delete email</li>
 *   <li>{@code POST /mail/emptyTrash} - Delete all trash items</li>
 *   <li>{@code POST /mail/bulk*} - Bulk operations for multiple emails</li>
 *   <li>{@code GET /mail/starred} - View starred emails</li>
 *   <li>{@code POST /mail/toggleStar} - Toggle star status</li>
 *   <li>{@code GET /mail/unreadCount} - Get unread count for badge</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see MailboxService
 * @see AttachmentService
 */
@Controller
@RequestMapping("/mail")
public class MailboxController {

    private final MailboxService mailboxService;
    private final AttachmentService attachmentService;

    /**
     * Constructs the MailboxController with required service dependencies.
     * 
     * @param mailboxService    service for mailbox operations
     * @param attachmentService service for attachment operations
     */
    public MailboxController(MailboxService mailboxService, AttachmentService attachmentService) {
        this.mailboxService = mailboxService;
        this.attachmentService = attachmentService;
    }

    /**
     * Displays the inbox page with optional search functionality.
     * 
     * @param model    the model to add attributes for the view
     * @param user     the authenticated user
     * @param q        optional search query to filter emails
     * @param pageable pagination parameters
     * @return the view name for the inbox page
     */
    @GetMapping("/inbox")
    public String inbox(Model model,
                        @AuthenticationPrincipal SecurityUser user,
                        @RequestParam(required = false) String q,
                        Pageable pageable) {

        if (q != null && !q.trim().isEmpty()) {
            model.addAttribute("page",
                mailboxService.searchInbox(user.getId(), q.trim(), pageable));
            model.addAttribute("searchQuery", q);
        } else {
            model.addAttribute("page",
                mailboxService.inbox(user.getId(), pageable));
        }

        return "mail/inbox";
    }

    /**
     * Displays the email preview page with full content and attachments.
     * 
     * @param id     the email ID to preview
     * @param source the source folder (inbox, sent, draft, etc.)
     * @param user   the authenticated user
     * @param model  the model to add attributes for the view
     * @return the view name for the preview page
     * @throws AccessDeniedException if user doesn't have access to the email
     */
    @GetMapping("/preview")
    public String preview(@RequestParam Long id,
                          @RequestParam(required = false, defaultValue = "inbox") String source,
                          @AuthenticationPrincipal SecurityUser user,
                          Model model) throws AccessDeniedException {

        Email email = mailboxService.findEmailForUser(id, user.getId());
        List<Attachment> attachments =
            attachmentService.findByEmailId(id);

        model.addAttribute("email", email);
        model.addAttribute("attachments", attachments);
        model.addAttribute("source", source);
        
        // Get recipient info for star status
        mailboxService.getRecipient(id, user.getId())
            .ifPresent(recipient -> model.addAttribute("recipient", recipient));
        
        // Get thread/conversation history
        if (email.getThreadId() != null) {
            List<Email> threadEmails = mailboxService.getConversationThread(email.getThreadId(), id);
            if (threadEmails != null && !threadEmails.isEmpty()) {
                model.addAttribute("threadEmails", threadEmails);
            }
        }
        
        return "mail/preview";
    }

    /**
     * Marks an email as read and sends read receipt if requested.
     * 
     * @param id   the email ID to mark as read
     * @param user the authenticated user
     * @return 200 OK response on success
     */
    @PostMapping("/markAsRead")
    @ResponseBody
    public ResponseEntity<Void> markAsRead(@RequestParam Long id,
                                           @AuthenticationPrincipal SecurityUser user) {
        mailboxService.markAsReadWithReceipt(id, user.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * Moves an email to the trash folder.
     * 
     * @param emailId the email ID to move to trash
     * @param user    the authenticated user
     * @return 200 OK response on success
     */
    @PostMapping("/moveToTrash")
    @ResponseBody
    public ResponseEntity<Void> moveToTrash(@RequestParam Long emailId,
                                            @AuthenticationPrincipal SecurityUser user) {
        mailboxService.moveToTrash(emailId, user.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * Restores an email from the trash folder.
     * 
     * @param emailId the email ID to restore
     * @param user    the authenticated user
     * @return 200 OK response on success
     */
    @PostMapping("/restoreFromTrash")
    @ResponseBody
    public ResponseEntity<Void> restoreFromTrash(@RequestParam Long emailId,
                                                 @AuthenticationPrincipal SecurityUser user) {
        mailboxService.restoreFromTrash(emailId, user.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * Permanently deletes an email.
     * 
     * <p>This action cannot be undone. The email and its attachments
     * are permanently removed from the database.</p>
     * 
     * @param emailId the email ID to permanently delete
     * @param user    the authenticated user
     * @return 200 OK response on success
     */
    @PostMapping("/permanentDelete")
    @ResponseBody
    public ResponseEntity<Void> permanentDelete(@RequestParam Long emailId,
                                                @AuthenticationPrincipal SecurityUser user) {
        mailboxService.permanentDelete(emailId, user.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * Empties the trash folder by permanently deleting all trashed emails.
     * 
     * <p>This action cannot be undone. All emails in trash are permanently removed.</p>
     * 
     * @param user the authenticated user
     * @return 200 OK response on success
     */
    @PostMapping("/emptyTrash")
    @ResponseBody
    public ResponseEntity<Void> emptyTrash(@AuthenticationPrincipal SecurityUser user) {
        mailboxService.emptyTrash(user.getId());
        return ResponseEntity.ok().build();
    }

    // ============================================
    // BULK OPERATIONS
    // ============================================
    
    /**
     * Moves multiple emails to the trash folder in bulk.
     * 
     * <p>Continues processing remaining emails if any individual operation fails.</p>
     * 
     * @param emailIds list of email IDs to move to trash
     * @param user     the authenticated user
     * @return 200 OK response on completion
     */
    @PostMapping("/bulkMoveToTrash")
    @ResponseBody
    public ResponseEntity<Void> bulkMoveToTrash(@RequestParam List<Long> emailIds,
                                                @AuthenticationPrincipal SecurityUser user) {
        for (Long emailId : emailIds) {
            try {
                mailboxService.moveToTrash(emailId, user.getId());
            } catch (Exception e) {
                // Continue with other emails if one fails
            }
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Permanently deletes multiple emails in bulk.
     * 
     * <p>This action cannot be undone. Continues processing remaining 
     * emails if any individual operation fails.</p>
     * 
     * @param emailIds list of email IDs to permanently delete
     * @param user     the authenticated user
     * @return 200 OK response on completion
     */
    @PostMapping("/bulkPermanentDelete")
    @ResponseBody
    public ResponseEntity<Void> bulkPermanentDelete(@RequestParam List<Long> emailIds,
                                                    @AuthenticationPrincipal SecurityUser user) {
        for (Long emailId : emailIds) {
            try {
                mailboxService.permanentDelete(emailId, user.getId());
            } catch (Exception e) {
                // Continue with other emails if one fails
            }
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Restores multiple emails from trash in bulk.
     * 
     * <p>Continues processing remaining emails if any individual operation fails.</p>
     * 
     * @param emailIds list of email IDs to restore
     * @param user     the authenticated user
     * @return 200 OK response on completion
     */
    @PostMapping("/bulkRestore")
    @ResponseBody
    public ResponseEntity<Void> bulkRestore(@RequestParam List<Long> emailIds,
                                            @AuthenticationPrincipal SecurityUser user) {
        for (Long emailId : emailIds) {
            try {
                mailboxService.restoreFromTrash(emailId, user.getId());
            } catch (Exception e) {
                // Continue with other emails if one fails
            }
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Marks multiple emails as read in bulk.
     * 
     * <p>Continues processing remaining emails if any individual operation fails.</p>
     * 
     * @param emailIds list of email IDs to mark as read
     * @param user     the authenticated user
     * @return 200 OK response on completion
     */
    @PostMapping("/bulkMarkRead")
    @ResponseBody
    public ResponseEntity<Void> bulkMarkRead(@RequestParam List<Long> emailIds,
                                             @AuthenticationPrincipal SecurityUser user) {
        for (Long emailId : emailIds) {
            try {
                mailboxService.markAsReadWithReceipt(emailId, user.getId());
            } catch (Exception e) {
                // Continue with other emails if one fails
            }
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Deletes a draft email.
     * 
     * <p>Only emails in DRAFT status can be deleted using this endpoint.</p>
     * 
     * @param id   the draft email ID to delete
     * @param user the authenticated user
     * @return 200 OK response on success
     */
    @PostMapping("/deleteDraft")
    @ResponseBody
    public ResponseEntity<Void> deleteDraft(@RequestParam Long id,
                                           @AuthenticationPrincipal SecurityUser user) {
        mailboxService.deleteDraft(id, user.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * Deletes multiple draft emails in bulk.
     * 
     * <p>Continues processing remaining drafts if any individual operation fails.</p>
     * 
     * @param emailIds list of draft email IDs to delete
     * @param user     the authenticated user
     * @return 200 OK response on completion
     */
    @PostMapping("/bulkDeleteDrafts")
    @ResponseBody
    public ResponseEntity<Void> bulkDeleteDrafts(@RequestParam List<Long> emailIds,
                                                 @AuthenticationPrincipal SecurityUser user) {
        for (Long emailId : emailIds) {
            try {
                mailboxService.deleteDraft(emailId, user.getId());
            } catch (Exception e) {
                // Continue with other drafts if one fails
            }
        }
        return ResponseEntity.ok().build();
    }

    // ============================================
    // STARRED EMAILS
    // ============================================

    /**
     * Displays the starred emails page.
     * 
     * @param model    the model to add attributes for the view
     * @param user     the authenticated user
     * @param pageable pagination parameters
     * @return the view name for the starred emails page
     */
    @GetMapping("/starred")
    public String starred(Model model,
                          @AuthenticationPrincipal SecurityUser user,
                          Pageable pageable) {
        model.addAttribute("page", mailboxService.starred(user.getId(), pageable));
        return "mail/starred";
    }

    /**
     * Toggles the starred status of an email.
     * 
     * @param emailId the email ID to toggle star
     * @param user    the authenticated user
     * @return the new starred status (true if starred, false if unstarred)
     */
    @PostMapping("/toggleStar")
    @ResponseBody
    public ResponseEntity<Boolean> toggleStar(@RequestParam Long emailId,
                                              @AuthenticationPrincipal SecurityUser user) {
        boolean starred = mailboxService.toggleStar(emailId, user.getId());
        return ResponseEntity.ok(starred);
    }

    // ============================================
    // UNREAD COUNT API
    // ============================================

    /**
     * Gets the unread email count for the authenticated user.
     * 
     * <p>Used by the frontend to display unread badge on inbox.</p>
     * 
     * @param user the authenticated user
     * @return the count of unread emails in inbox
     */
    @GetMapping("/unreadCount")
    @ResponseBody
    public ResponseEntity<Long> getUnreadCount(@AuthenticationPrincipal SecurityUser user) {
        long count = mailboxService.countUnreadInbox(user.getId());
        return ResponseEntity.ok(count);
    }

}

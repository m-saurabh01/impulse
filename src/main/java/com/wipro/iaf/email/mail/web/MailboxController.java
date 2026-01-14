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

@Controller
@RequestMapping("/mail")
public class MailboxController {

    private final MailboxService mailboxService;
    private final AttachmentService attachmentService;

    public MailboxController(MailboxService mailboxService,AttachmentService attachmentService) {
        this.mailboxService = mailboxService;
        this.attachmentService=attachmentService;
    }

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

    @PostMapping("/markAsRead")
    @ResponseBody
    public ResponseEntity<Void> markAsRead(@RequestParam Long id,
                                           @AuthenticationPrincipal SecurityUser user) {
        mailboxService.markAsRead(id, user.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/moveToTrash")
    @ResponseBody
    public ResponseEntity<Void> moveToTrash(@RequestParam Long emailId,
                                            @AuthenticationPrincipal SecurityUser user) {
        mailboxService.moveToTrash(emailId, user.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/restoreFromTrash")
    @ResponseBody
    public ResponseEntity<Void> restoreFromTrash(@RequestParam Long emailId,
                                                 @AuthenticationPrincipal SecurityUser user) {
        mailboxService.restoreFromTrash(emailId, user.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/permanentDelete")
    @ResponseBody
    public ResponseEntity<Void> permanentDelete(@RequestParam Long emailId,
                                                @AuthenticationPrincipal SecurityUser user) {
        mailboxService.permanentDelete(emailId, user.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/emptyTrash")
    @ResponseBody
    public ResponseEntity<Void> emptyTrash(@AuthenticationPrincipal SecurityUser user) {
        mailboxService.emptyTrash(user.getId());
        return ResponseEntity.ok().build();
    }

    // ============================================
    // BULK OPERATIONS
    // ============================================
    
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

    @PostMapping("/bulkMarkRead")
    @ResponseBody
    public ResponseEntity<Void> bulkMarkRead(@RequestParam List<Long> emailIds,
                                             @AuthenticationPrincipal SecurityUser user) {
        for (Long emailId : emailIds) {
            try {
                mailboxService.markAsRead(emailId, user.getId());
            } catch (Exception e) {
                // Continue with other emails if one fails
            }
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deleteDraft")
    @ResponseBody
    public ResponseEntity<Void> deleteDraft(@RequestParam Long id,
                                           @AuthenticationPrincipal SecurityUser user) {
        mailboxService.deleteDraft(id, user.getId());
        return ResponseEntity.ok().build();
    }

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

    @GetMapping("/starred")
    public String starred(Model model,
                          @AuthenticationPrincipal SecurityUser user,
                          Pageable pageable) {
        model.addAttribute("page", mailboxService.starred(user.getId(), pageable));
        return "mail/starred";
    }

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

    @GetMapping("/unreadCount")
    @ResponseBody
    public ResponseEntity<Long> getUnreadCount(@AuthenticationPrincipal SecurityUser user) {
        long count = mailboxService.countUnreadInbox(user.getId());
        return ResponseEntity.ok(count);
    }

}

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
                        Pageable pageable) {

        model.addAttribute("page",
            mailboxService.inbox(user.getId(), pageable));

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


}

package com.wipro.iaf.email.mail.web;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wipro.iaf.email.mail.entity.EmailRecipientId;
import com.wipro.iaf.email.mail.service.MailboxService;
import com.wipro.iaf.email.security.SecurityUser;

@Controller
@RequestMapping("/mail")
public class FolderController {

    private final MailboxService mailboxService;

    public FolderController(MailboxService mailboxService) {
        this.mailboxService = mailboxService;
    }
    
    @GetMapping("/sent")
    public String sent(Model model,
                       @AuthenticationPrincipal SecurityUser user,
                       Pageable pageable) {

        model.addAttribute("page",
            mailboxService.sent(user.getId(), pageable));
        return "mail/sent";
    }

    @GetMapping("/drafts")
    public String drafts(Model model,
                         @AuthenticationPrincipal SecurityUser user,
                         Pageable pageable) {

        model.addAttribute("page",
            mailboxService.drafts(user.getId(), pageable));
        return "mail/drafts";
    }

    @GetMapping("/trash")
    public String trash(Model model,
                        @AuthenticationPrincipal SecurityUser user,
                        Pageable pageable) {

        model.addAttribute("page",
            mailboxService.trash(user.getId(), pageable));
        return "mail/trash";
    }

    @PostMapping("/trash/delete")
    public String delete(@RequestParam Long emailId,
                         @AuthenticationPrincipal SecurityUser user) {

        mailboxService.moveToTrash(
            new EmailRecipientId(emailId, user.getId()));

        return "redirect:/mail/inbox";
    }

    @PostMapping("/trash/restore")
    public String restore(@RequestParam Long emailId,
                           @AuthenticationPrincipal SecurityUser user) {

        mailboxService.restoreFromTrash(
            new EmailRecipientId(emailId, user.getId()));

        return "redirect:/mail/trash";
    }
}

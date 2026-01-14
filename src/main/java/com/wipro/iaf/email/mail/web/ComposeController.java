package com.wipro.iaf.email.mail.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wipro.iaf.email.attachment.repo.AttachmentRepository;
import com.wipro.iaf.email.mail.dto.ComposeEmailRequest;
import com.wipro.iaf.email.mail.entity.Email;
import com.wipro.iaf.email.mail.repo.EmailRepository;
import com.wipro.iaf.email.mail.service.EmailComposeService;
import com.wipro.iaf.email.security.SecurityUser;



@Controller
@RequestMapping("/mail")
public class ComposeController {

    private final EmailComposeService composeService;
    private final EmailRepository emailRepo;
    private final AttachmentRepository attachmentRepo;

    public ComposeController(EmailComposeService composeService, 
                             EmailRepository emailRepo,
                             AttachmentRepository attachmentRepo) {
        this.composeService = composeService;
        this.emailRepo = emailRepo;
        this.attachmentRepo = attachmentRepo;
    }

    @GetMapping("/compose")
    public String compose(@RequestParam(required = false) Long id,
                          @AuthenticationPrincipal SecurityUser user,
                          Model model) {
        if (id != null) {
            Email draft = emailRepo.findByIdAndSenderId(id, user.getId())
                    .orElse(null);
            model.addAttribute("draft", draft);
        }
        return "mail/compose";
    }

    @PostMapping("/send")
    public String send(@ModelAttribute ComposeEmailRequest req,
                       @AuthenticationPrincipal SecurityUser user) {

        composeService.composeAndSend(req, user);
        return "redirect:/mail/inbox";
    }

    @PostMapping("/discard")
    @Transactional
    public String discard(@RequestParam Long id,
                          @AuthenticationPrincipal SecurityUser user) {
        emailRepo.findByIdAndSenderId(id, user.getId())
                .ifPresent(email -> {
                    attachmentRepo.deleteByEmailId(email.getId());
                    emailRepo.delete(email);
                });
        return "redirect:/mail/drafts";
    }
}

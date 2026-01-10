package com.wipro.iaf.email.mail.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wipro.iaf.email.mail.dto.ComposeEmailRequest;
import com.wipro.iaf.email.mail.service.EmailComposeService;
import com.wipro.iaf.email.security.SecurityUser;

@Controller
@RequestMapping("/mail")
public class ComposeController {

    private final EmailComposeService composeService;

    public ComposeController(EmailComposeService composeService) {
        this.composeService = composeService;
    }

    @GetMapping("/compose")
    public String compose() {
        return "mail/compose";
    }

    @PostMapping("/send")
    public String send(@ModelAttribute ComposeEmailRequest req,
                       @AuthenticationPrincipal SecurityUser user) {

        composeService.composeAndSend(req, user);
        return "redirect:/mail/inbox";
    }
}

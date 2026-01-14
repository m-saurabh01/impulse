package com.wipro.iaf.email.config;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.wipro.iaf.email.mail.service.MailboxService;
import com.wipro.iaf.email.security.SecurityUser;

/**
 * Global model attributes for all controllers
 * Adds unread email count to all pages that use the mail layout
 */
@ControllerAdvice
public class GlobalModelAdvice {

    private final MailboxService mailboxService;

    public GlobalModelAdvice(MailboxService mailboxService) {
        this.mailboxService = mailboxService;
    }

    @ModelAttribute("inboxUnreadCount")
    public Long inboxUnreadCount(@AuthenticationPrincipal SecurityUser user) {
        if (user != null) {
            return mailboxService.countUnreadInbox(user.getId());
        }
        return 0L;
    }
}

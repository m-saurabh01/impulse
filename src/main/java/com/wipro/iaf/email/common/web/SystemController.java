package com.wipro.iaf.email.common.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.wipro.iaf.email.security.SecurityUser;

@Controller
public class SystemController {

    /**
     * Secret diagnostics page - Easter egg
     * Only accessible for logged-in users
     */
    @GetMapping("/system/diagnostics")
    public String diagnosticsPage(@AuthenticationPrincipal SecurityUser user) {
        if (user == null) {
            return "redirect:/auth/login";
        }
        return "common/diagnostics";
    }
}

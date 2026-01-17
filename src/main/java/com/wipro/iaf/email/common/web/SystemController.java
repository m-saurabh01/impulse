package com.wipro.iaf.email.common.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.wipro.iaf.email.security.SecurityUser;

/**
 * Controller for system utilities and hidden features.
 * 
 * <p>Provides access to system diagnostics and other
 * administrative utilities. Contains Easter egg features
 * accessible only to authenticated users.</p>
 * 
 * <p>Endpoints:
 * <ul>
 *   <li>{@code GET /system/diagnostics} - System diagnostics page (Easter egg)</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 */
@Controller
public class SystemController {

    /**
     * Displays the secret diagnostics page.
     * 
     * <p>This is an Easter egg feature that shows system diagnostics
     * information. Only accessible to authenticated users.</p>
     * 
     * @param user the authenticated user
     * @return the view name for diagnostics, or redirect to login if not authenticated
     */
    @GetMapping("/system/diagnostics")
    public String diagnosticsPage(@AuthenticationPrincipal SecurityUser user) {
        if (user == null) {
            return "redirect:/auth/login";
        }
        return "common/diagnostics";
    }
}

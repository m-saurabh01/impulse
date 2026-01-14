package com.wipro.iaf.email.user.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wipro.iaf.email.security.SecurityUser;
import com.wipro.iaf.email.user.dto.ChangePasswordRequest;
import com.wipro.iaf.email.user.dto.ProfileUpdateRequest;
import com.wipro.iaf.email.user.entity.User;
import com.wipro.iaf.email.user.service.ProfileService;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * Show profile page
     */
    @GetMapping
    public String showProfile(@AuthenticationPrincipal SecurityUser securityUser,
                              Model model) {
        User user = profileService.getUserById(securityUser.getId());
        model.addAttribute("user", user);
        return "profile/settings";
    }

    /**
     * Update profile (display name, signature)
     */
    @PostMapping("/update")
    public String updateProfile(@AuthenticationPrincipal SecurityUser securityUser,
                                ProfileUpdateRequest request,
                                RedirectAttributes redirectAttributes) {
        try {
            profileService.updateProfile(securityUser.getId(), request);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/profile";
    }

    /**
     * Change password
     */
    @PostMapping("/changePassword")
    public String changePassword(@AuthenticationPrincipal SecurityUser securityUser,
                                 ChangePasswordRequest request,
                                 RedirectAttributes redirectAttributes) {
        try {
            profileService.changePassword(securityUser.getId(), request);
            redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/profile";
    }

    /**
     * Get current user info (for AJAX)
     */
    @GetMapping("/info")
    @ResponseBody
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal SecurityUser securityUser) {
        User user = profileService.getUserById(securityUser.getId());
        return ResponseEntity.ok(new UserInfoResponse(
            user.getEmail(),
            user.getDisplayNameOrEmail(),
            user.getSignature()
        ));
    }

    /**
     * Simple response class for user info
     */
    private static class UserInfoResponse {
        private final String email;
        private final String displayName;
        private final String signature;

        public UserInfoResponse(String email, String displayName, String signature) {
            this.email = email;
            this.displayName = displayName;
            this.signature = signature;
        }

        public String getEmail() { return email; }
        public String getDisplayName() { return displayName; }
        public String getSignature() { return signature; }
    }
}

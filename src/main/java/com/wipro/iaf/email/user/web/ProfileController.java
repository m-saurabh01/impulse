package com.wipro.iaf.email.user.web;

import java.util.List;

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
import com.wipro.iaf.email.user.entity.UserAchievement;
import com.wipro.iaf.email.user.service.AchievementService;
import com.wipro.iaf.email.user.service.AchievementService.AchievementWithStatus;
import com.wipro.iaf.email.user.service.ProfileService;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final AchievementService achievementService;

    public ProfileController(ProfileService profileService, AchievementService achievementService) {
        this.profileService = profileService;
        this.achievementService = achievementService;
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
     * Get all achievements with status (unlocked/locked)
     */
    @GetMapping("/achievements")
    @ResponseBody
    public ResponseEntity<List<AchievementWithStatus>> getAchievements(
            @AuthenticationPrincipal SecurityUser securityUser) {
        List<AchievementWithStatus> achievements = 
            achievementService.getAchievementsWithStatus(securityUser.getId());
        return ResponseEntity.ok(achievements);
    }

    /**
     * Get only unlocked achievements for badge display
     */
    @GetMapping("/achievements/unlocked")
    @ResponseBody
    public ResponseEntity<List<UserAchievement>> getUnlockedAchievements(
            @AuthenticationPrincipal SecurityUser securityUser) {
        List<UserAchievement> unlocked = 
            achievementService.getUserAchievements(securityUser.getId());
        return ResponseEntity.ok(unlocked);
    }

    /**
     * Get count and total points for achievements (for profile summary)
     */
    @GetMapping("/achievements/summary")
    @ResponseBody
    public ResponseEntity<AchievementSummary> getAchievementSummary(
            @AuthenticationPrincipal SecurityUser securityUser) {
        List<UserAchievement> unlocked = 
            achievementService.getUserAchievements(securityUser.getId());
        
        int totalPoints = unlocked.stream()
            .mapToInt(ua -> ua.getAchievement().getPoints())
            .sum();
        
        int totalAchievements = achievementService.getAchievementsWithStatus(securityUser.getId()).size();
        
        return ResponseEntity.ok(new AchievementSummary(
            unlocked.size(),
            totalAchievements,
            totalPoints
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

    /**
     * Response class for achievement summary
     */
    private static class AchievementSummary {
        private final int unlockedCount;
        private final int totalCount;
        private final int totalPoints;

        public AchievementSummary(int unlockedCount, int totalCount, int totalPoints) {
            this.unlockedCount = unlockedCount;
            this.totalCount = totalCount;
            this.totalPoints = totalPoints;
        }

        public int getUnlockedCount() { return unlockedCount; }
        public int getTotalCount() { return totalCount; }
        public int getTotalPoints() { return totalPoints; }
    }
}

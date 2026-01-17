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

/**
 * Controller for user profile management and achievement display.
 * 
 * <p>Handles all user profile-related operations including viewing and updating
 * profile settings, changing passwords, and managing achievements. All endpoints
 * require authentication.</p>
 * 
 * <p>Endpoints:
 * <ul>
 *   <li>{@code GET /profile} - Display profile settings page</li>
 *   <li>{@code POST /profile/update} - Update display name and signature</li>
 *   <li>{@code POST /profile/changePassword} - Change user password</li>
 *   <li>{@code GET /profile/info} - Get user info as JSON (AJAX)</li>
 *   <li>{@code GET /profile/achievements} - Get all achievements with unlock status</li>
 *   <li>{@code GET /profile/achievements/unlocked} - Get only unlocked achievements</li>
 *   <li>{@code GET /profile/achievements/summary} - Get achievement count and points</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see ProfileService
 * @see AchievementService
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final AchievementService achievementService;

    /**
     * Constructs the ProfileController with required service dependencies.
     * 
     * @param profileService     service for profile data operations
     * @param achievementService service for achievement operations
     */
    public ProfileController(ProfileService profileService, AchievementService achievementService) {
        this.profileService = profileService;
        this.achievementService = achievementService;
    }

    /**
     * Displays the user profile settings page.
     * 
     * @param securityUser the authenticated user from Spring Security
     * @param model        the model to add attributes for the view
     * @return the view name for the profile settings page
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
     * Permanently delete user account
     */
    @PostMapping("/deleteAccount")
    public String deleteAccount(@AuthenticationPrincipal SecurityUser securityUser,
                                @org.springframework.web.bind.annotation.RequestParam String password,
                                RedirectAttributes redirectAttributes,
                                javax.servlet.http.HttpServletRequest request) {
        try {
            profileService.deleteAccount(securityUser.getId(), password);
            // Invalidate session and logout
            request.getSession().invalidate();
            return "redirect:/login?accountDeleted=true";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/profile";
        }
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

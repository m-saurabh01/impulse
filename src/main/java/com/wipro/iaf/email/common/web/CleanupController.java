package com.wipro.iaf.email.common.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wipro.iaf.email.common.service.DataCleanupScheduler;
import com.wipro.iaf.email.common.service.DataCleanupService;
import com.wipro.iaf.email.common.service.DataCleanupService.CleanupResult;
import com.wipro.iaf.email.security.SecurityUser;

/**
 * Controller for data cleanup administration.
 * 
 * <p>Provides endpoints for:
 * <ul>
 *   <li>Viewing cleanup statistics (preview)</li>
 *   <li>Running manual cleanup tasks</li>
 *   <li>Viewing cleanup configuration</li>
 * </ul>
 * </p>
 * 
 * <p>Access is restricted to admin users only.</p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-18
 */
@Controller
@RequestMapping("/admin/cleanup")
public class CleanupController {

    private static final Logger log = LoggerFactory.getLogger(CleanupController.class);

    private final DataCleanupService cleanupService;
    private final DataCleanupScheduler cleanupScheduler;

    public CleanupController(
            DataCleanupService cleanupService, 
            DataCleanupScheduler cleanupScheduler) {
        this.cleanupService = cleanupService;
        this.cleanupScheduler = cleanupScheduler;
    }

    /**
     * Shows the cleanup administration page with statistics.
     */
    @GetMapping
    public String showCleanupPage(
            @AuthenticationPrincipal SecurityUser user,
            Model model) {

        log.info("User {} accessing cleanup admin page", user.getUsername());

        // Get cleanup statistics (preview)
        CleanupResult stats = cleanupService.getCleanupStatistics();

        model.addAttribute("stats", stats);
        model.addAttribute("schedulerEnabled", cleanupScheduler.isSchedulerEnabled());
        model.addAttribute("trashRetentionDays", cleanupService.getTrashRetentionDays());
        model.addAttribute("draftRetentionDays", cleanupService.getDraftRetentionDays());
        model.addAttribute("deletedUserRetentionDays", cleanupService.getDeletedUserRetentionDays());

        return "admin/cleanup";
    }

    /**
     * Runs full cleanup and redirects back to admin page.
     */
    @PostMapping("/run-full")
    public String runFullCleanup(
            @AuthenticationPrincipal SecurityUser user,
            RedirectAttributes redirectAttributes) {

        log.info("User {} triggered full cleanup", user.getUsername());

        try {
            CleanupResult result = cleanupService.runFullCleanup();

            String message = String.format(
                "Cleanup completed: %d trashed emails, %d drafts, %d orphaned files deleted. %s reclaimed.",
                result.getTrashedEmailsDeleted(),
                result.getDraftsDeleted(),
                result.getOrphanedFilesDeleted(),
                result.getFormattedDiskSpace());

            redirectAttributes.addFlashAttribute("successMessage", message);

            if (!result.getErrors().isEmpty()) {
                redirectAttributes.addFlashAttribute("warningMessage", 
                    result.getErrors().size() + " errors occurred during cleanup.");
            }
        } catch (Exception e) {
            log.error("Full cleanup failed", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Cleanup failed: " + e.getMessage());
        }

        return "redirect:/admin/cleanup";
    }

    /**
     * Runs trash cleanup only.
     */
    @PostMapping("/run-trash")
    public String runTrashCleanup(
            @AuthenticationPrincipal SecurityUser user,
            RedirectAttributes redirectAttributes) {

        log.info("User {} triggered trash cleanup", user.getUsername());

        try {
            CleanupResult result = cleanupService.cleanupTrashedEmails();
            redirectAttributes.addFlashAttribute("successMessage", 
                "Trash cleanup completed: " + result.getTrashedEmailsDeleted() + " emails deleted.");
        } catch (Exception e) {
            log.error("Trash cleanup failed", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Trash cleanup failed: " + e.getMessage());
        }

        return "redirect:/admin/cleanup";
    }

    /**
     * Runs draft cleanup only.
     */
    @PostMapping("/run-drafts")
    public String runDraftCleanup(
            @AuthenticationPrincipal SecurityUser user,
            RedirectAttributes redirectAttributes) {

        log.info("User {} triggered draft cleanup", user.getUsername());

        try {
            CleanupResult result = cleanupService.cleanupOldDrafts();
            redirectAttributes.addFlashAttribute("successMessage", 
                "Draft cleanup completed: " + result.getDraftsDeleted() + " drafts deleted.");
        } catch (Exception e) {
            log.error("Draft cleanup failed", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Draft cleanup failed: " + e.getMessage());
        }

        return "redirect:/admin/cleanup";
    }

    /**
     * Runs attachment sync only.
     */
    @PostMapping("/run-attachments")
    public String runAttachmentSync(
            @AuthenticationPrincipal SecurityUser user,
            RedirectAttributes redirectAttributes) {

        log.info("User {} triggered attachment sync", user.getUsername());

        try {
            CleanupResult result = cleanupService.cleanupOrphanedAttachments();
            redirectAttributes.addFlashAttribute("successMessage", 
                String.format("Attachment sync completed: %d files, %d records deleted. %s reclaimed.",
                    result.getOrphanedFilesDeleted(),
                    result.getOrphanedRecordsDeleted(),
                    result.getFormattedDiskSpace()));
        } catch (Exception e) {
            log.error("Attachment sync failed", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Attachment sync failed: " + e.getMessage());
        }

        return "redirect:/admin/cleanup";
    }

    /**
     * Runs user anonymization only.
     */
    @PostMapping("/run-anonymize")
    public String runUserAnonymization(
            @AuthenticationPrincipal SecurityUser user,
            RedirectAttributes redirectAttributes) {

        log.info("User {} triggered user anonymization", user.getUsername());

        try {
            CleanupResult result = cleanupService.anonymizeDeletedUsers();
            redirectAttributes.addFlashAttribute("successMessage", 
                "User anonymization completed: " + result.getDeletedUsersAnonymized() + " users anonymized.");
        } catch (Exception e) {
            log.error("User anonymization failed", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "User anonymization failed: " + e.getMessage());
        }

        return "redirect:/admin/cleanup";
    }

    /**
     * API endpoint to get cleanup stats as JSON.
     */
    @GetMapping("/stats")
    @ResponseBody
    public CleanupResult getCleanupStats() {
        return cleanupService.getCleanupStatistics();
    }
}

package com.wipro.iaf.email.mail.web;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wipro.iaf.email.mail.entity.EmailRecipientId;
import com.wipro.iaf.email.mail.service.MailboxService;
import com.wipro.iaf.email.security.SecurityUser;

/**
 * Controller for mail folder views.
 * 
 * <p>Handles displaying sent items, drafts, trash, and snoozed emails.
 * Also provides snooze/unsnooze functionality for deferring emails.</p>
 * 
 * <p>Endpoints:
 * <ul>
 *   <li>{@code GET /mail/sent} - View sent emails</li>
 *   <li>{@code GET /mail/drafts} - View draft emails</li>
 *   <li>{@code GET /mail/trash} - View trashed emails</li>
 *   <li>{@code POST /mail/trash/delete} - Delete email (move to trash)</li>
 *   <li>{@code POST /mail/trash/restore} - Restore email from trash</li>
 *   <li>{@code GET /mail/snoozed} - View snoozed emails</li>
 *   <li>{@code POST /mail/snooze} - Snooze an email</li>
 *   <li>{@code POST /mail/unsnooze} - Unsnooze an email</li>
 *   <li>{@code GET /mail/snooze/count} - Get snoozed email count</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see MailboxService
 */
@Controller
@RequestMapping("/mail")
public class FolderController {

    private final MailboxService mailboxService;

    /**
     * Constructs the FolderController with required dependencies.
     * 
     * @param mailboxService service for mailbox operations
     */
    public FolderController(MailboxService mailboxService) {
        this.mailboxService = mailboxService;
    }
    
    /**
     * Displays the sent emails folder.
     * 
     * @param model    the model to add attributes for the view
     * @param user     the authenticated user
     * @param pageable pagination parameters
     * @return the view name for the sent folder
     */
    @GetMapping("/sent")
    public String sent(Model model,
                       @AuthenticationPrincipal SecurityUser user,
                       Pageable pageable) {

        model.addAttribute("page",
            mailboxService.sent(user.getId(), pageable));
        return "mail/sent";
    }

    /**
     * Displays the drafts folder.
     * 
     * @param model    the model to add attributes for the view
     * @param user     the authenticated user
     * @param pageable pagination parameters
     * @return the view name for the drafts folder
     */
    @GetMapping("/drafts")
    public String drafts(Model model,
                         @AuthenticationPrincipal SecurityUser user,
                         Pageable pageable) {

        model.addAttribute("page",
            mailboxService.drafts(user.getId(), pageable));
        return "mail/drafts";
    }

    /**
     * Displays the trash folder.
     * 
     * @param model    the model to add attributes for the view
     * @param user     the authenticated user
     * @param pageable pagination parameters
     * @return the view name for the trash folder
     */
    @GetMapping("/trash")
    public String trash(Model model,
                        @AuthenticationPrincipal SecurityUser user,
                        Pageable pageable) {

        model.addAttribute("page",
            mailboxService.trash(user.getId(), pageable));
        return "mail/trash";
    }

    /**
     * Moves an email to the trash folder.
     * 
     * @param emailId the email ID to move to trash
     * @param user    the authenticated user
     * @return redirect to inbox
     */
    @PostMapping("/trash/delete")
    public String delete(@RequestParam Long emailId,
                         @AuthenticationPrincipal SecurityUser user) {

        mailboxService.moveToTrash(
            new EmailRecipientId(emailId, user.getId()));

        return "redirect:/mail/inbox";
    }

    /**
     * Restores an email from the trash folder.
     * 
     * @param emailId the email ID to restore
     * @param user    the authenticated user
     * @return redirect to trash folder
     */
    @PostMapping("/trash/restore")
    public String restore(@RequestParam Long emailId,
                           @AuthenticationPrincipal SecurityUser user) {

        mailboxService.restoreFromTrash(
            new EmailRecipientId(emailId, user.getId()));

        return "redirect:/mail/trash";
    }

    /**
     * Displays the snoozed emails folder.
     * 
     * @param model    the model to add attributes for the view
     * @param user     the authenticated user
     * @param pageable pagination parameters
     * @return the view name for the snoozed folder
     */
    @GetMapping("/snoozed")
    public String snoozed(Model model,
                          @AuthenticationPrincipal SecurityUser user,
                          Pageable pageable) {
        model.addAttribute("page",
            mailboxService.snoozed(user.getId(), pageable));
        return "mail/snoozed";
    }

    /**
     * Snoozes an email until a specified time.
     * 
     * <p>Supports preset times (later_today, tomorrow, next_week, 1_hour, 4_hours)
     * or a custom datetime.</p>
     * 
     * @param request the snooze request containing email ID and time
     * @param user    the authenticated user
     * @return response with success status and snoozed until time
     */
    @PostMapping("/snooze")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> snoozeEmail(
            @RequestBody SnoozeRequest request,
            @AuthenticationPrincipal SecurityUser user) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            LocalDateTime snoozedUntil;
            
            // Parse preset or custom time
            if ("custom".equals(request.preset)) {
                snoozedUntil = LocalDateTime.parse(request.customTime, 
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } else {
                snoozedUntil = calculateSnoozeTime(request.preset);
            }
            
            mailboxService.snoozeEmail(request.emailId, user.getId(), snoozedUntil);
            
            response.put("success", true);
            response.put("snoozedUntil", snoozedUntil.format(
                DateTimeFormatter.ofPattern("MMM dd, h:mm a")));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Unsnoozes an email, returning it to the inbox.
     * 
     * @param emailId the email ID to unsnooze
     * @param user    the authenticated user
     * @return response with success status
     */
    @PostMapping("/unsnooze")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> unsnoozeEmail(
            @RequestParam Long emailId,
            @AuthenticationPrincipal SecurityUser user) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            mailboxService.unsnoozeEmail(emailId, user.getId());
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Gets the count of snoozed emails for sidebar badge.
     * 
     * @param user the authenticated user
     * @return the count of snoozed emails
     */
    @GetMapping("/snooze/count")
    @ResponseBody
    public ResponseEntity<Long> getSnoozeCount(@AuthenticationPrincipal SecurityUser user) {
        return ResponseEntity.ok(mailboxService.countSnoozed(user.getId()));
    }

    /**
     * Calculates snooze time based on preset option.
     * 
     * <p>Presets:
     * <ul>
     *   <li>later_today - 5 PM today, or 3 hours from now if past 5 PM</li>
     *   <li>tomorrow - 9 AM tomorrow</li>
     *   <li>next_week - 9 AM next Monday</li>
     *   <li>1_hour - 1 hour from now</li>
     *   <li>4_hours - 4 hours from now</li>
     * </ul>
     * </p>
     * 
     * @param preset the preset option name
     * @return the calculated LocalDateTime
     */
    private LocalDateTime calculateSnoozeTime(String preset) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (preset) {
            case "later_today":
                // Snooze until 5 PM today, or 3 hours from now if past 5 PM
                LocalDateTime fivePm = now.withHour(17).withMinute(0).withSecond(0);
                return now.isAfter(fivePm) ? now.plusHours(3) : fivePm;
                
            case "tomorrow":
                // Tomorrow at 9 AM
                return now.plusDays(1).withHour(9).withMinute(0).withSecond(0);
                
            case "next_week":
                // Next Monday at 9 AM
                int daysUntilMonday = (8 - now.getDayOfWeek().getValue()) % 7;
                if (daysUntilMonday == 0) daysUntilMonday = 7;
                return now.plusDays(daysUntilMonday).withHour(9).withMinute(0).withSecond(0);
                
            case "1_hour":
                return now.plusHours(1);
                
            case "4_hours":
                return now.plusHours(4);
                
            default:
                return now.plusDays(1).withHour(9).withMinute(0).withSecond(0);
        }
    }

    /**
     * Request body for snooze operation.
     * 
     * <p>Contains the email ID, preset type, and optional custom time.</p>
     * 
     * @author Saurabh Mishra
     * @version 1.0
     * @since 2026-01-01
     */
    public static class SnoozeRequest {
        /** The ID of the email to snooze */
        public Long emailId;
        /** The preset snooze option (later_today, tomorrow, etc.) */
        public String preset;
        /** Custom datetime in ISO format when preset is "custom" */
        public String customTime;
    }
}

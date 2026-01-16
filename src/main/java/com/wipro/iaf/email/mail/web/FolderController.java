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

@Controller
@RequestMapping("/mail")
public class FolderController {

    private final MailboxService mailboxService;

    public FolderController(MailboxService mailboxService) {
        this.mailboxService = mailboxService;
    }
    
    @GetMapping("/sent")
    public String sent(Model model,
                       @AuthenticationPrincipal SecurityUser user,
                       Pageable pageable) {

        model.addAttribute("page",
            mailboxService.sent(user.getId(), pageable));
        return "mail/sent";
    }

    @GetMapping("/drafts")
    public String drafts(Model model,
                         @AuthenticationPrincipal SecurityUser user,
                         Pageable pageable) {

        model.addAttribute("page",
            mailboxService.drafts(user.getId(), pageable));
        return "mail/drafts";
    }

    @GetMapping("/trash")
    public String trash(Model model,
                        @AuthenticationPrincipal SecurityUser user,
                        Pageable pageable) {

        model.addAttribute("page",
            mailboxService.trash(user.getId(), pageable));
        return "mail/trash";
    }

    @PostMapping("/trash/delete")
    public String delete(@RequestParam Long emailId,
                         @AuthenticationPrincipal SecurityUser user) {

        mailboxService.moveToTrash(
            new EmailRecipientId(emailId, user.getId()));

        return "redirect:/mail/inbox";
    }

    @PostMapping("/trash/restore")
    public String restore(@RequestParam Long emailId,
                           @AuthenticationPrincipal SecurityUser user) {

        mailboxService.restoreFromTrash(
            new EmailRecipientId(emailId, user.getId()));

        return "redirect:/mail/trash";
    }

    /**
     * View snoozed emails
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
     * Snooze an email (AJAX)
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
     * Unsnooze an email (AJAX)
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
     * Get snooze count (for sidebar badge)
     */
    @GetMapping("/snooze/count")
    @ResponseBody
    public ResponseEntity<Long> getSnoozeCount(@AuthenticationPrincipal SecurityUser user) {
        return ResponseEntity.ok(mailboxService.countSnoozed(user.getId()));
    }

    /**
     * Calculate snooze time based on preset
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
     * Request body for snooze operation
     */
    public static class SnoozeRequest {
        public Long emailId;
        public String preset;
        public String customTime;
    }
}

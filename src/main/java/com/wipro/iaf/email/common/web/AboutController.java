package com.wipro.iaf.email.common.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wipro.iaf.email.common.entity.Feedback;
import com.wipro.iaf.email.common.entity.Feedback.FeedbackType;
import com.wipro.iaf.email.common.service.FeedbackService;
import com.wipro.iaf.email.security.SecurityUser;
import com.wipro.iaf.email.user.entity.User;
import com.wipro.iaf.email.user.repo.UserRepository;

/**
 * Controller for the About page and feedback management.
 * 
 * <p>Handles displaying application information, feature descriptions,
 * and user feedback submission. Also provides admin functionality
 * for managing feedback submissions.</p>
 * 
 * <p>Endpoints:
 * <ul>
 *   <li>{@code GET /about} - About Impulse page</li>
 *   <li>{@code POST /about/feedback} - Submit feedback</li>
 *   <li>{@code GET /about/feedback/admin} - Admin feedback list (admin only)</li>
 *   <li>{@code POST /about/feedback/markRead} - Mark feedback as read (admin only)</li>
 *   <li>{@code POST /about/feedback/delete} - Delete feedback (admin only)</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see FeedbackService
 * @see Feedback
 */
@Controller
@RequestMapping("/about")
public class AboutController {

    private final FeedbackService feedbackService;
    private final UserRepository userRepo;

    /**
     * Constructs the AboutController with required dependencies.
     * 
     * @param feedbackService service for feedback operations
     * @param userRepo        repository for user operations
     */
    public AboutController(FeedbackService feedbackService, UserRepository userRepo) {
        this.feedbackService = feedbackService;
        this.userRepo = userRepo;
    }

    /**
     * Displays the About Impulse page.
     * 
     * <p>Shows application features, version information, and a feedback form.
     * If the user is authenticated, pre-fills their name and email.</p>
     * 
     * @param model the model to add attributes for the view
     * @param user  the authenticated user (may be null)
     * @return the view name for the about page
     */
    @GetMapping
    public String aboutPage(Model model, @AuthenticationPrincipal SecurityUser user) {
        if (user != null) {
            model.addAttribute("userName", user.getDisplayName() != null ? user.getDisplayName() : user.getUsername());
            model.addAttribute("userEmail", user.getUsername());
            // Check if user is admin
            boolean isAdmin = user.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            model.addAttribute("isAdmin", isAdmin);
        }
        return "about/index";
    }

    /**
     * Submits user feedback.
     * 
     * <p>Accepts feedback of various types (feature request, bug report,
     * suggestion, etc.) and stores it for admin review.</p>
     * 
     * <p>Contains an Easter egg: submitting a bug report with subject
     * "Critical" and message "Hey Buddy" redirects to diagnostics.</p>
     * 
     * @param name               submitter's name
     * @param email              submitter's email
     * @param type               feedback type (feature-request, bug-report, etc.)
     * @param subject            feedback subject
     * @param message            feedback message content
     * @param securityUser       the authenticated user (may be null)
     * @param redirectAttributes attributes for redirect flash messages
     * @return redirect to about page with success/error message
     */
    @PostMapping("/feedback")
    public String submitFeedback(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String type,
            @RequestParam String subject,
            @RequestParam String message,
            @AuthenticationPrincipal SecurityUser securityUser,
            RedirectAttributes redirectAttributes) {
        
        try {
            FeedbackType feedbackType = FeedbackType.valueOf(type.toUpperCase().replace("-", "_"));
            
            // Easter Egg: Secret combination triggers diagnostics page
            if (feedbackType == FeedbackType.BUG_REPORT 
                    && "Critical".equalsIgnoreCase(subject.trim()) 
                    && "Hey Buddy".equalsIgnoreCase(message.trim())
                    && securityUser != null) {
                return "redirect:/system/diagnostics";
            }
            
            User user = securityUser != null ? userRepo.findById(securityUser.getId()).orElse(null) : null;
            
            feedbackService.submitFeedback(user, name, email, feedbackType, subject, message);
            redirectAttributes.addFlashAttribute("successMessage", "Thank you for your feedback! We appreciate your input.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to submit feedback. Please try again.");
        }
        
        return "redirect:/about";
    }

    /**
     * Displays the admin feedback management page.
     * 
     * <p>Lists all feedback submissions with pagination and optional
     * filtering by type. Only accessible to users with ROLE_ADMIN.</p>
     * 
     * @param page  the page number (0-based)
     * @param size  the page size
     * @param type  optional filter by feedback type
     * @param model the model to add attributes for the view
     * @param user  the authenticated user
     * @return the view name for feedback admin, or redirect if not admin
     */
    @GetMapping("/feedback/admin")
    public String feedbackAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type,
            Model model,
            @AuthenticationPrincipal SecurityUser user) {
        
        // Check if user is admin
        if (user == null || !user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/mail/inbox";
        }
        
        Page<Feedback> feedbackPage;
        if (type != null && !type.isEmpty()) {
            FeedbackType feedbackType = FeedbackType.valueOf(type.toUpperCase().replace("-", "_"));
            feedbackPage = feedbackService.getFeedbackByType(feedbackType, PageRequest.of(page, size));
        } else {
            feedbackPage = feedbackService.getAllFeedback(PageRequest.of(page, size));
        }
        
        model.addAttribute("feedbackPage", feedbackPage);
        model.addAttribute("unreadCount", feedbackService.getUnreadCount());
        model.addAttribute("selectedType", type);
        
        return "about/feedback-admin";
    }

    /**
     * Marks a feedback item as read.
     * 
     * <p>Only accessible to users with ROLE_ADMIN.</p>
     * 
     * @param id   the feedback ID to mark as read
     * @param user the authenticated user
     * @return 200 OK on success, 403 if not admin
     */
    @PostMapping("/feedback/markRead")
    @ResponseBody
    public ResponseEntity<?> markAsRead(@RequestParam Long id, 
                                        @AuthenticationPrincipal SecurityUser user) {
        if (user == null || !user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).build();
        }
        
        feedbackService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Deletes a feedback item.
     * 
     * <p>Only accessible to users with ROLE_ADMIN.</p>
     * 
     * @param id   the feedback ID to delete
     * @param user the authenticated user
     * @return 200 OK on success, 403 if not admin
     */
    @PostMapping("/feedback/delete")
    @ResponseBody
    public ResponseEntity<?> deleteFeedback(@RequestParam Long id,
                                            @AuthenticationPrincipal SecurityUser user) {
        if (user == null || !user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).build();
        }
        
        feedbackService.deleteFeedback(id);
        return ResponseEntity.ok().build();
    }
}

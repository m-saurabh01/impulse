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

@Controller
@RequestMapping("/about")
public class AboutController {

    private final FeedbackService feedbackService;
    private final UserRepository userRepo;

    public AboutController(FeedbackService feedbackService, UserRepository userRepo) {
        this.feedbackService = feedbackService;
        this.userRepo = userRepo;
    }

    /**
     * About Impulse page - shows features and feedback form
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
     * Submit feedback
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
            User user = securityUser != null ? userRepo.findById(securityUser.getId()).orElse(null) : null;
            
            feedbackService.submitFeedback(user, name, email, feedbackType, subject, message);
            redirectAttributes.addFlashAttribute("successMessage", "Thank you for your feedback! We appreciate your input.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to submit feedback. Please try again.");
        }
        
        return "redirect:/about";
    }

    /**
     * Admin page to view all feedback (only for admin/developer)
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
     * Mark feedback as read
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
     * Delete feedback
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

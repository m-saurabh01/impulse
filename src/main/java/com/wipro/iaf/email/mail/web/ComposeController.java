package com.wipro.iaf.email.mail.web;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wipro.iaf.email.attachment.repo.AttachmentRepository;
import com.wipro.iaf.email.mail.dto.ComposeEmailRequest;
import com.wipro.iaf.email.mail.entity.Email;
import com.wipro.iaf.email.mail.repo.EmailRepository;
import com.wipro.iaf.email.mail.service.EmailComposeResult;
import com.wipro.iaf.email.mail.service.EmailComposeService;
import com.wipro.iaf.email.security.SecurityUser;
import com.wipro.iaf.email.user.entity.User;
import com.wipro.iaf.email.user.repo.UserRepository;

/**
 * Controller for email composition operations.
 * 
 * <p>Handles composing new emails, replying, reply all, forwarding,
 * draft management, and email sending. Supports conversation threading
 * and user signatures.</p>
 * 
 * <p>Endpoints:
 * <ul>
 *   <li>{@code GET /mail/compose} - Display compose form (new, reply, forward, draft)</li>
 *   <li>{@code POST /mail/send} - Send or save email as draft</li>
 *   <li>{@code POST /mail/discard} - Discard a draft</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see EmailComposeService
 * @see Email
 */
@Controller
@RequestMapping("/mail")
public class ComposeController {

    private final EmailComposeService composeService;
    private final EmailRepository emailRepo;
    private final AttachmentRepository attachmentRepo;
    private final UserRepository userRepo;

    /**
     * Constructs the ComposeController with required dependencies.
     * 
     * @param composeService service for email composition
     * @param emailRepo      repository for email operations
     * @param attachmentRepo repository for attachment operations
     * @param userRepo       repository for user operations
     */
    public ComposeController(EmailComposeService composeService, 
                             EmailRepository emailRepo,
                             AttachmentRepository attachmentRepo,
                             UserRepository userRepo) {
        this.composeService = composeService;
        this.emailRepo = emailRepo;
        this.attachmentRepo = attachmentRepo;
        this.userRepo = userRepo;
    }

    /**
     * Displays the email compose form.
     * 
     * <p>Supports multiple modes:
     * <ul>
     *   <li>New email - with optional pre-filled to/subject/body</li>
     *   <li>Draft editing - loads existing draft by id</li>
     *   <li>Reply - sets recipient and quoted body</li>
     *   <li>Reply All - includes CC recipients</li>
     *   <li>Forward - includes original content and attachments</li>
     * </ul>
     * </p>
     * 
     * @param id       optional draft email ID to continue editing
     * @param replyTo  optional email ID to reply to
     * @param replyAll true to include all original recipients
     * @param forward  optional email ID to forward
     * @param to       optional pre-filled recipient
     * @param subject  optional pre-filled subject
     * @param body     optional pre-filled body
     * @param user     the authenticated user
     * @param model    the model to add attributes for the view
     * @return the view name for the compose page
     */
    @GetMapping("/compose")
    public String compose(@RequestParam(required = false) Long id,
                          @RequestParam(required = false) Long replyTo,
                          @RequestParam(required = false) Boolean replyAll,
                          @RequestParam(required = false) Long forward,
                          @RequestParam(required = false) String to,
                          @RequestParam(required = false) String subject,
                          @RequestParam(required = false) String body,
                          @AuthenticationPrincipal SecurityUser user,
                          Model model) {
        
        // Handle direct compose with to/subject/body params (e.g., from feedback reply)
        if (to != null && !to.isEmpty()) {
            model.addAttribute("directTo", to);
        }
        if (subject != null && !subject.isEmpty()) {
            model.addAttribute("directSubject", subject);
        }
        if (body != null && !body.isEmpty()) {
            model.addAttribute("directBody", body);
        }
        
        if (id != null) {
            Email draft = emailRepo.findByIdAndSenderId(id, user.getId())
                    .orElse(null);
            model.addAttribute("draft", draft);
        }
        
        // Handle Forward
        if (forward != null) {
            Email originalEmail = emailRepo.findById(forward).orElse(null);
            if (originalEmail != null) {
                // Set subject with "Fwd:" prefix if not already present
                String fwdSubject = originalEmail.getSubject();
                if (fwdSubject == null || fwdSubject.isEmpty()) {
                    fwdSubject = "(No subject)";
                }
                if (!fwdSubject.toLowerCase().startsWith("fwd:")) {
                    fwdSubject = "Fwd: " + fwdSubject;
                }
                model.addAttribute("forwardSubject", fwdSubject);
                
                // Build forwarded message body
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a");
                String formattedDate = originalEmail.getCreatedAt().format(formatter);
                
                StringBuilder forwardBody = new StringBuilder();
                forwardBody.append("<br><br>");
                forwardBody.append("<div style=\"border-top: 1px solid #ccc; padding-top: 15px; margin-top: 15px;\">");
                forwardBody.append("<p style=\"color: #666; margin: 0 0 10px 0;\"><strong>---------- Forwarded message ----------</strong></p>");
                forwardBody.append("<p style=\"color: #666; margin: 0;\"><strong>From:</strong> ")
                           .append(originalEmail.getSender().getEmail()).append("</p>");
                forwardBody.append("<p style=\"color: #666; margin: 0;\"><strong>Date:</strong> ")
                           .append(formattedDate).append("</p>");
                forwardBody.append("<p style=\"color: #666; margin: 0;\"><strong>Subject:</strong> ")
                           .append(originalEmail.getSubject() != null ? originalEmail.getSubject() : "(No subject)").append("</p>");
                forwardBody.append("<p style=\"color: #666; margin: 0 0 15px 0;\"><strong>To:</strong> ")
                           .append(originalEmail.getToRecipients()).append("</p>");
                forwardBody.append("<div>").append(originalEmail.getBodyHtml() != null ? originalEmail.getBodyHtml() : "").append("</div>");
                forwardBody.append("</div>");
                model.addAttribute("forwardBody", forwardBody.toString());
                
                // Include original attachments for forwarding
                List<com.wipro.iaf.email.attachment.entity.Attachment> originalAttachments = attachmentRepo.findByEmailId(forward);
                if (!originalAttachments.isEmpty()) {
                    model.addAttribute("forwardAttachments", originalAttachments);
                }
            }
        }
        
        // Handle Reply/Reply All
        if (replyTo != null) {
            Email originalEmail = emailRepo.findById(replyTo).orElse(null);
            if (originalEmail != null) {
                // Set "To" as original sender
                model.addAttribute("replyTo", originalEmail.getSender().getEmail());
                
                // Set subject with "Re:" prefix if not already present
                String replySubject = originalEmail.getSubject();
                if (replySubject == null || replySubject.isEmpty()) {
                    replySubject = "(No subject)";
                }
                if (!replySubject.toLowerCase().startsWith("re:")) {
                    replySubject = "Re: " + replySubject;
                }
                model.addAttribute("replySubject", replySubject);
                
                // For Reply All, include CC recipients (excluding current user)
                if (Boolean.TRUE.equals(replyAll)) {
                    StringBuilder ccRecipients = new StringBuilder();
                    // Add original To recipients (except current user)
                    String toRecipients = originalEmail.getToRecipients();
                    if (toRecipients != null && !toRecipients.isEmpty()) {
                        for (String email : toRecipients.split(",")) {
                            email = email.trim();
                            if (!email.equalsIgnoreCase(user.getEmail())) {
                                if (ccRecipients.length() > 0) ccRecipients.append(",");
                                ccRecipients.append(email);
                            }
                        }
                    }
                    // Add original CC recipients (except current user)
                    String ccOriginal = originalEmail.getCcRecipients();
                    if (ccOriginal != null && !ccOriginal.isEmpty()) {
                        for (String email : ccOriginal.split(",")) {
                            email = email.trim();
                            if (!email.equalsIgnoreCase(user.getEmail())) {
                                if (ccRecipients.length() > 0) ccRecipients.append(",");
                                ccRecipients.append(email);
                            }
                        }
                    }
                    if (ccRecipients.length() > 0) {
                        model.addAttribute("replyCc", ccRecipients.toString());
                    }
                }
                
                // Build quoted reply body with formatted date
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a");
                String formattedDate = originalEmail.getCreatedAt().format(formatter);
                
                StringBuilder replyBody = new StringBuilder();
                replyBody.append("<br><br>");
                replyBody.append("<details style=\"margin-top: 20px;\">");
                replyBody.append("<summary style=\"cursor: pointer; color: #6c5ce7; font-weight: 500; margin-bottom: 10px;\">")
                         .append("On ").append(formattedDate).append(", ")
                         .append(originalEmail.getSender().getEmail()).append(" wrote:</summary>");
                replyBody.append("<div style=\"padding-left: 10px; border-left: 3px solid #6c5ce7; color: #666;\">");
                replyBody.append(originalEmail.getBodyHtml() != null ? originalEmail.getBodyHtml() : "");
                replyBody.append("</div>");
                replyBody.append("</details>");
                model.addAttribute("replyBody", replyBody.toString());
                
                // Set thread ID for conversation threading
                Long threadId = originalEmail.getThreadId();
                if (threadId == null) {
                    threadId = originalEmail.getId(); // Original email becomes thread root
                }
                model.addAttribute("replyThreadId", threadId);
            }
        }
        
        // Load user's signature
        User currentUser = userRepo.findById(user.getId()).orElse(null);
        if (currentUser != null && currentUser.getSignature() != null) {
            model.addAttribute("signature", currentUser.getSignature());
        }
        
        return "mail/compose";
    }

    /**
     * Sends an email or saves it as a draft.
     * 
     * <p>If the request has draft=true, saves as draft. Otherwise,
     * validates and sends the email to all recipients.</p>
     * 
     * @param req                the compose email request with recipients and content
     * @param user               the authenticated sender
     * @param redirectAttributes attributes for redirect flash messages
     * @return redirect to drafts page if saving draft, or inbox if sent
     */
    @PostMapping("/send")
    public String send(@ModelAttribute ComposeEmailRequest req,
                       @AuthenticationPrincipal SecurityUser user,
                       RedirectAttributes redirectAttributes) {

        EmailComposeResult result = composeService.composeAndSendWithValidation(req, user);
        
        if (!result.isSuccess()) {
            redirectAttributes.addFlashAttribute("error", result.getErrorMessage());
            return "redirect:/mail/compose";
        }
        
        if (req.isDraft()) {
            redirectAttributes.addFlashAttribute("success", "Draft saved successfully");
            return "redirect:/mail/drafts";
        }
        
        redirectAttributes.addFlashAttribute("success", "Email sent successfully");
        return "redirect:/mail/inbox";
    }

    /**
     * Discards a draft email permanently.
     * 
     * <p>Deletes the draft and all associated attachments.</p>
     * 
     * @param id   the draft email ID to discard
     * @param user the authenticated user
     * @return redirect to drafts page
     */
    @PostMapping("/discard")
    @Transactional
    public String discard(@RequestParam Long id,
                          @AuthenticationPrincipal SecurityUser user) {
        emailRepo.findByIdAndSenderId(id, user.getId())
                .ifPresent(email -> {
                    attachmentRepo.deleteByEmailId(email.getId());
                    emailRepo.delete(email);
                });
        return "redirect:/mail/drafts";
    }
}

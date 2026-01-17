package com.wipro.iaf.email.mail.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wipro.iaf.email.mail.entity.QuickReply;
import com.wipro.iaf.email.mail.service.QuickReplyService;
import com.wipro.iaf.email.security.SecurityUser;

/**
 * Controller for managing quick reply templates.
 * 
 * <p>Provides REST API endpoints for CRUD operations on quick replies.
 * Quick replies are reusable email templates that users can insert
 * when composing emails. Users can create up to a configured maximum
 * number of quick replies.</p>
 * 
 * <p>Endpoints:
 * <ul>
 *   <li>{@code GET /mail/quick-replies} - List all user's quick replies</li>
 *   <li>{@code POST /mail/quick-replies} - Create a new quick reply</li>
 *   <li>{@code GET /mail/quick-replies/{id}} - Get a specific quick reply</li>
 *   <li>{@code PUT /mail/quick-replies/{id}} - Update a quick reply</li>
 *   <li>{@code DELETE /mail/quick-replies/{id}} - Delete a quick reply</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see QuickReplyService
 * @see QuickReply
 */
@Controller
@RequestMapping("/mail/quick-replies")
public class QuickReplyController {

    private final QuickReplyService quickReplyService;

    /**
     * Constructs the QuickReplyController with required dependencies.
     * 
     * @param quickReplyService service for quick reply operations
     */
    public QuickReplyController(QuickReplyService quickReplyService) {
        this.quickReplyService = quickReplyService;
    }

    /**
     * Gets all quick replies for the authenticated user.
     * 
     * @param user the authenticated user
     * @return list of quick reply entities
     */
    @GetMapping
    @ResponseBody
    public ResponseEntity<List<QuickReply>> getQuickReplies(@AuthenticationPrincipal SecurityUser user) {
        return ResponseEntity.ok(quickReplyService.getQuickReplies(user.getId()));
    }

    /**
     * Creates a new quick reply template.
     * 
     * @param title   the quick reply title
     * @param content the quick reply content/body
     * @param user    the authenticated user
     * @return the created quick reply or error if limit reached
     */
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createQuickReply(
            @RequestParam String title,
            @RequestParam String content,
            @AuthenticationPrincipal SecurityUser user) {
        try {
            QuickReply qr = quickReplyService.createQuickReply(user.getId(), title, content);
            return ResponseEntity.ok(qr);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(errorMap(e.getMessage()));
        }
    }

    /**
     * Updates an existing quick reply template.
     * 
     * @param id      the quick reply ID to update
     * @param title   the new title
     * @param content the new content
     * @param user    the authenticated user
     * @return the updated quick reply or 404 if not found
     */
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateQuickReply(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String content,
            @AuthenticationPrincipal SecurityUser user) {
        try {
            QuickReply qr = quickReplyService.updateQuickReply(user.getId(), id, title, content);
            return ResponseEntity.ok(qr);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a quick reply template.
     * 
     * @param id   the quick reply ID to delete
     * @param user the authenticated user
     * @return 200 OK on success
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteQuickReply(
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUser user) {
        quickReplyService.deleteQuickReply(user.getId(), id);
        return ResponseEntity.ok().build();
    }

    /**
     * Gets a specific quick reply by ID.
     * 
     * @param id   the quick reply ID
     * @param user the authenticated user
     * @return the quick reply or 404 if not found
     */
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getQuickReply(
            @PathVariable Long id,
            @AuthenticationPrincipal SecurityUser user) {
        try {
            QuickReply qr = quickReplyService.getQuickReply(user.getId(), id);
            return ResponseEntity.ok(qr);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Creates an error response map.
     * 
     * @param message the error message
     * @return map containing the error message
     */
    private Map<String, String> errorMap(String message) {
        Map<String, String> map = new HashMap<>();
        map.put("error", message);
        return map;
    }
}

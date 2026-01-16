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

@Controller
@RequestMapping("/mail/quick-replies")
public class QuickReplyController {

    private final QuickReplyService quickReplyService;

    public QuickReplyController(QuickReplyService quickReplyService) {
        this.quickReplyService = quickReplyService;
    }

    /**
     * Get all quick replies for the current user (JSON)
     */
    @GetMapping
    @ResponseBody
    public ResponseEntity<List<QuickReply>> getQuickReplies(@AuthenticationPrincipal SecurityUser user) {
        return ResponseEntity.ok(quickReplyService.getQuickReplies(user.getId()));
    }

    /**
     * Create a new quick reply
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
     * Update an existing quick reply
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
     * Delete a quick reply
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
     * Get a specific quick reply content
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

    private Map<String, String> errorMap(String message) {
        Map<String, String> map = new HashMap<>();
        map.put("error", message);
        return map;
    }
}

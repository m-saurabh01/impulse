package com.wipro.iaf.email.mail.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wipro.iaf.email.mail.entity.EmailLabel;
import com.wipro.iaf.email.mail.entity.Label;
import com.wipro.iaf.email.mail.service.LabelService;
import com.wipro.iaf.email.security.SecurityUser;

/**
 * Controller for managing email labels.
 * 
 * <p>Provides CRUD operations for labels and functionality to
 * add/remove labels from emails. Labels are user-specific and
 * can be color-coded for organization.</p>
 * 
 * <p>Endpoints:
 * <ul>
 *   <li>{@code GET /mail/labels/list} - Get all user's labels</li>
 *   <li>{@code POST /mail/labels/create} - Create a new label</li>
 *   <li>{@code POST /mail/labels/update} - Update label name/color</li>
 *   <li>{@code POST /mail/labels/delete} - Delete a label</li>
 *   <li>{@code GET /mail/labels/email/{id}} - Get labels for an email</li>
 *   <li>{@code POST /mail/labels/email/add} - Add label to email</li>
 *   <li>{@code POST /mail/labels/email/remove} - Remove label from email</li>
 *   <li>{@code GET /mail/labels/manage} - Label management page</li>
 *   <li>{@code GET /mail/labels/view/{id}} - View emails by label</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see LabelService
 * @see Label
 * @see EmailLabel
 */
@Controller
@RequestMapping("/mail/labels")
public class LabelController {

    private final LabelService labelService;

    /**
     * Constructs the LabelController with required dependencies.
     * 
     * @param labelService service for label operations
     */
    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    /**
     * Gets all labels for the current user.
     * 
     * <p>Used to populate dropdown menus for label selection.</p>
     * 
     * @param user the authenticated user
     * @return list of label data as maps with id, name, and color
     */
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getLabels(
            @AuthenticationPrincipal SecurityUser user) {
        List<Label> labels = labelService.getLabelsForUser(user.getId());
        List<Map<String, Object>> result = labels.stream()
            .map(this::mapLabel)
            .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * Creates a new label for the user.
     * 
     * @param name  the label name
     * @param color the label color (hex code)
     * @param user  the authenticated user
     * @return the created label data or error message
     */
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createLabel(
            @RequestParam String name,
            @RequestParam String color,
            @AuthenticationPrincipal SecurityUser user) {
        try {
            Label label = labelService.createLabel(user.getId(), name, color);
            return ResponseEntity.ok(mapLabel(label));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(errorMap(e.getMessage()));
        }
    }

    /**
     * Updates an existing label's name and/or color.
     * 
     * @param labelId the label ID to update
     * @param name    the new label name
     * @param color   the new label color (hex code)
     * @param user    the authenticated user
     * @return the updated label data or error message
     */
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updateLabel(
            @RequestParam Long labelId,
            @RequestParam String name,
            @RequestParam String color,
            @AuthenticationPrincipal SecurityUser user) {
        try {
            Label label = labelService.updateLabel(labelId, user.getId(), name, color);
            return ResponseEntity.ok(mapLabel(label));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(errorMap(e.getMessage()));
        }
    }

    /**
     * Deletes a label and removes it from all emails.
     * 
     * @param labelId the label ID to delete
     * @param user    the authenticated user
     * @return 200 OK on success or error message
     */
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<?> deleteLabel(
            @RequestParam Long labelId,
            @AuthenticationPrincipal SecurityUser user) {
        try {
            labelService.deleteLabel(labelId, user.getId());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(errorMap(e.getMessage()));
        }
    }

    /**
     * Gets all labels applied to a specific email.
     * 
     * @param emailId the email ID
     * @param user    the authenticated user
     * @return list of label data for the email
     */
    @GetMapping("/email/{emailId}")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getEmailLabels(
            @PathVariable Long emailId,
            @AuthenticationPrincipal SecurityUser user) {
        List<EmailLabel> emailLabels = labelService.getLabelsForEmail(emailId, user.getId());
        List<Map<String, Object>> result = emailLabels.stream()
            .map(el -> mapLabel(el.getLabel()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * Adds a label to an email.
     * 
     * @param emailId the email ID
     * @param labelId the label ID to add
     * @param user    the authenticated user
     * @return 200 OK on success or error message
     */
    @PostMapping("/email/add")
    @ResponseBody
    public ResponseEntity<?> addLabelToEmail(
            @RequestParam Long emailId,
            @RequestParam Long labelId,
            @AuthenticationPrincipal SecurityUser user) {
        try {
            labelService.addLabelToEmail(emailId, labelId, user.getId());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(errorMap(e.getMessage()));
        }
    }

    /**
     * Removes a label from an email.
     * 
     * @param emailId the email ID
     * @param labelId the label ID to remove
     * @param user    the authenticated user
     * @return 200 OK on success or error message
     */
    @PostMapping("/email/remove")
    @ResponseBody
    public ResponseEntity<?> removeLabelFromEmail(
            @RequestParam Long emailId,
            @RequestParam Long labelId,
            @AuthenticationPrincipal SecurityUser user) {
        try {
            labelService.removeLabelFromEmail(emailId, labelId, user.getId());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(errorMap(e.getMessage()));
        }
    }

    /**
     * Displays the labels management page.
     * 
     * @param model the model to add attributes for the view
     * @param user  the authenticated user
     * @return the view name for the labels management page
     */
    @GetMapping("/manage")
    public String manageLabels(Model model, @AuthenticationPrincipal SecurityUser user) {
        model.addAttribute("labels", labelService.getLabelsForUser(user.getId()));
        return "mail/labels";
    }

    /**
     * Displays emails filtered by a specific label.
     * 
     * @param labelId the label ID to filter by
     * @param page    the page number (0-based)
     * @param size    the page size
     * @param model   the model to add attributes for the view
     * @param user    the authenticated user
     * @return the view name for label-filtered emails, or redirect if label not found
     */
    @GetMapping("/view/{labelId}")
    public String viewEmailsByLabel(
            @PathVariable Long labelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Model model,
            @AuthenticationPrincipal SecurityUser user) {
        
        Label label = labelService.getLabelById(labelId, user.getId());
        if (label == null) {
            return "redirect:/mail/inbox";
        }
        
        Page<EmailLabel> emailLabels = labelService.getEmailsByLabel(labelId, user.getId(), PageRequest.of(page, size));
        
        model.addAttribute("label", label);
        model.addAttribute("page", emailLabels);
        model.addAttribute("currentFolder", "label-" + labelId);
        model.addAttribute("allLabels", labelService.getLabelsForUser(user.getId()));
        
        return "mail/labelView";
    }

    /**
     * Maps a Label entity to a Map for JSON response.
     * 
     * @param label the label entity
     * @return map containing id, name, and color
     */
    private Map<String, Object> mapLabel(Label label) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", label.getId());
        map.put("name", label.getName());
        map.put("color", label.getColor());
        return map;
    }

    /**
     * Creates an error response map.
     * 
     * @param message the error message
     * @return map containing the error message
     */
    private Map<String, String> errorMap(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}

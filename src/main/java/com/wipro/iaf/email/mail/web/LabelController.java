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

@Controller
@RequestMapping("/mail/labels")
public class LabelController {

    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    /**
     * Get all labels for the current user (for dropdown menus)
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
     * Create a new label
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
     * Update a label
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
     * Delete a label
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
     * Get labels for a specific email
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
     * Add a label to an email
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
     * Remove a label from an email
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
     * Labels management page
     */
    @GetMapping("/manage")
    public String manageLabels(Model model, @AuthenticationPrincipal SecurityUser user) {
        model.addAttribute("labels", labelService.getLabelsForUser(user.getId()));
        return "mail/labels";
    }

    /**
     * View emails by label
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

    private Map<String, Object> mapLabel(Label label) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", label.getId());
        map.put("name", label.getName());
        map.put("color", label.getColor());
        return map;
    }

    private Map<String, String> errorMap(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}

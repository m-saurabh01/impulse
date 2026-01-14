package com.wipro.iaf.email.user.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.wipro.iaf.email.security.SecurityUser;
import com.wipro.iaf.email.user.entity.Contact;
import com.wipro.iaf.email.user.service.ContactService;

@Controller
@RequestMapping("/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    /**
     * Contacts page
     */
    @GetMapping
    public String contacts(Model model, @AuthenticationPrincipal SecurityUser user) {
        model.addAttribute("contacts", contactService.getAllContacts(user.getId()));
        return "contacts/list";
    }

    /**
     * Get all contacts as JSON (for autocomplete)
     */
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getContacts(
            @AuthenticationPrincipal SecurityUser user) {
        List<Contact> contacts = contactService.getAllContacts(user.getId());
        return ResponseEntity.ok(mapContacts(contacts));
    }

    /**
     * Search contacts (for autocomplete)
     */
    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> searchContacts(
            @RequestParam String q,
            @AuthenticationPrincipal SecurityUser user) {
        List<Contact> contacts = contactService.searchContacts(user.getId(), q);
        return ResponseEntity.ok(mapContacts(contacts));
    }

    /**
     * Create a new contact
     */
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createContact(
            @RequestParam String email,
            @RequestParam(required = false) String displayName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String notes,
            @AuthenticationPrincipal SecurityUser user) {
        try {
            Contact contact = contactService.createContact(
                user.getId(), email, displayName, phone, company, notes);
            return ResponseEntity.ok(mapContact(contact));
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Update a contact
     */
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updateContact(
            @RequestParam Long contactId,
            @RequestParam String email,
            @RequestParam(required = false) String displayName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String notes,
            @AuthenticationPrincipal SecurityUser user) {
        try {
            Contact contact = contactService.updateContact(
                contactId, user.getId(), email, displayName, phone, company, notes);
            return ResponseEntity.ok(mapContact(contact));
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Delete a contact
     */
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<?> deleteContact(
            @RequestParam Long contactId,
            @AuthenticationPrincipal SecurityUser user) {
        try {
            contactService.deleteContact(contactId, user.getId());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Toggle favorite status
     */
    @PostMapping("/toggleFavorite")
    @ResponseBody
    public ResponseEntity<Boolean> toggleFavorite(
            @RequestParam Long contactId,
            @AuthenticationPrincipal SecurityUser user) {
        boolean favorite = contactService.toggleFavorite(contactId, user.getId());
        return ResponseEntity.ok(favorite);
    }

    /**
     * Get a single contact
     */
    @GetMapping("/{contactId}")
    @ResponseBody
    public ResponseEntity<?> getContact(
            @PathVariable Long contactId,
            @AuthenticationPrincipal SecurityUser user) {
        return contactService.getContact(contactId, user.getId())
            .map(c -> ResponseEntity.ok(mapContact(c)))
            .orElse(ResponseEntity.notFound().build());
    }

    private List<Map<String, Object>> mapContacts(List<Contact> contacts) {
        return contacts.stream()
            .map(this::mapContact)
            .collect(Collectors.toList());
    }

    private Map<String, Object> mapContact(Contact c) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", c.getId());
        map.put("email", c.getEmail());
        map.put("displayName", c.getDisplayName() != null ? c.getDisplayName() : "");
        map.put("phone", c.getPhone() != null ? c.getPhone() : "");
        map.put("company", c.getCompany() != null ? c.getCompany() : "");
        map.put("notes", c.getNotes() != null ? c.getNotes() : "");
        map.put("favorite", c.isFavorite());
        return map;
    }
}

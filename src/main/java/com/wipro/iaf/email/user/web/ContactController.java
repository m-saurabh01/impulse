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

/**
 * Controller for managing user contacts (address book).
 * 
 * <p>Provides endpoints for CRUD operations on contacts and search functionality
 * for email autocomplete. Each user has their own private address book with
 * contacts only accessible by them.</p>
 * 
 * <p>Endpoints:
 * <ul>
 *   <li>{@code GET /contacts} - Display contacts page</li>
 *   <li>{@code GET /contacts/list} - Get all contacts as JSON (for UI)</li>
 *   <li>{@code GET /contacts/search?q=} - Search contacts for autocomplete</li>
 *   <li>{@code POST /contacts/create} - Create a new contact</li>
 *   <li>{@code POST /contacts/update} - Update existing contact</li>
 *   <li>{@code POST /contacts/delete} - Delete a contact</li>
 *   <li>{@code POST /contacts/toggleFavorite} - Toggle favorite status</li>
 *   <li>{@code GET /contacts/{id}} - Get single contact details</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see Contact
 * @see ContactService
 */
@Controller
@RequestMapping("/contacts")
public class ContactController {

    private final ContactService contactService;

    /**
     * Constructs the ContactController with required service dependency.
     * 
     * @param contactService service for contact data operations
     */
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    /**
     * Displays the contacts page with all user contacts.
     * 
     * @param model the model to add attributes for the view
     * @param user  the authenticated user from Spring Security
     * @return the view name for the contacts list page
     */
    @GetMapping
    public String contacts(Model model, @AuthenticationPrincipal SecurityUser user) {
        model.addAttribute("contacts", contactService.getAllContacts(user.getId()));
        return "contacts/list";
    }

    /**
     * Retrieves all contacts for the current user as JSON.
     * 
     * <p>Used by the frontend to populate the contacts list in the UI.</p>
     * 
     * @param user the authenticated user from Spring Security
     * @return ResponseEntity containing list of contact maps with id, email, displayName, etc.
     */
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getContacts(
            @AuthenticationPrincipal SecurityUser user) {
        List<Contact> contacts = contactService.getAllContacts(user.getId());
        return ResponseEntity.ok(mapContacts(contacts));
    }

    /**
     * Searches contacts by email or display name for autocomplete.
     * 
     * <p>Performs partial, case-insensitive matching. Results prioritize
     * favorites and are sorted alphabetically.</p>
     * 
     * @param q    the search query
     * @param user the authenticated user
     * @return ResponseEntity containing list of matching contacts
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
     * Creates a new contact in the user's address book.
     * 
     * @param email       the email address of the contact (required)
     * @param displayName optional display name
     * @param phone       optional phone number
     * @param company     optional company name
     * @param notes       optional notes about the contact
     * @param user        the authenticated user
     * @return ResponseEntity containing the created contact or error message
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

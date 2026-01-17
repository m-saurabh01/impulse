package com.wipro.iaf.email.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wipro.iaf.email.user.entity.Contact;
import com.wipro.iaf.email.user.entity.User;
import com.wipro.iaf.email.user.repo.ContactRepository;
import com.wipro.iaf.email.user.repo.UserRepository;

/**
 * Service class for managing user contacts in the PulseMail application.
 * <p>
 * This service provides comprehensive contact management functionality including:
 * <ul>
 *   <li>CRUD operations for contacts with additional metadata (phone, company, notes)</li>
 *   <li>Favorite contacts management for quick access</li>
 *   <li>Contact search by name, email, or other fields</li>
 *   <li>Automatic contact creation when sending emails to new recipients</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-16
 * @see Contact
 * @see User
 */
@Service
public class ContactService {

    private final ContactRepository contactRepo;
    private final UserRepository userRepo;

    /**
     * Constructs a new ContactService with required dependencies.
     * 
     * @param contactRepo repository for contact CRUD operations
     * @param userRepo repository for user lookups
     */
    public ContactService(ContactRepository contactRepo, UserRepository userRepo) {
        this.contactRepo = contactRepo;
        this.userRepo = userRepo;
    }

    /**
     * Retrieves all contacts for a user, ordered alphabetically by display name.
     * 
     * @param userId the ID of the user whose contacts to retrieve
     * @return list of contacts sorted by display name ascending
     */
    @Transactional(readOnly = true)
    public List<Contact> getAllContacts(Long userId) {
        return contactRepo.findByUserIdOrderByDisplayNameAsc(userId);
    }

    /**
     * Retrieves all favorite contacts for a user.
     * 
     * @param userId the ID of the user whose favorite contacts to retrieve
     * @return list of contacts marked as favorites
     */
    @Transactional(readOnly = true)
    public List<Contact> getFavoriteContacts(Long userId) {
        return contactRepo.findByUserIdAndFavoriteTrue(userId);
    }

    /**
     * Searches contacts by query string across name, email, and other fields.
     * 
     * @param userId the ID of the user whose contacts to search
     * @param query the search query string
     * @return list of contacts matching the search criteria
     */
    @Transactional(readOnly = true)
    public List<Contact> searchContacts(Long userId, String query) {
        return contactRepo.searchContacts(userId, query);
    }

    /**
     * Retrieves a specific contact by ID for a user.
     * 
     * @param contactId the ID of the contact to retrieve
     * @param userId the ID of the contact owner
     * @return Optional containing the Contact if found and owned by user
     */
    @Transactional(readOnly = true)
    public Optional<Contact> getContact(Long contactId, Long userId) {
        return contactRepo.findByIdAndUserId(contactId, userId);
    }

    /**
     * Creates a new contact for a user with all provided details.
     * 
     * @param userId the ID of the user creating the contact
     * @param email the email address of the contact
     * @param displayName the display name for the contact
     * @param phone the phone number (optional)
     * @param company the company name (optional)
     * @param notes additional notes about the contact (optional)
     * @return the newly created Contact entity
     * @throws IllegalArgumentException if user not found or contact email already exists
     */
    @Transactional
    public Contact createContact(Long userId, String email, String displayName, 
                                  String phone, String company, String notes) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if contact already exists
        if (contactRepo.existsByUserIdAndEmailIgnoreCase(userId, email)) {
            throw new IllegalArgumentException("Contact with this email already exists");
        }

        Contact contact = new Contact();
        contact.setUser(user);
        contact.setEmail(email);
        contact.setDisplayName(displayName);
        contact.setPhone(phone);
        contact.setCompany(company);
        contact.setNotes(notes);

        return contactRepo.save(contact);
    }

    /**
     * Updates an existing contact with new information.
     * 
     * @param contactId the ID of the contact to update
     * @param userId the ID of the contact owner (for authorization)
     * @param email the new email address
     * @param displayName the new display name
     * @param phone the new phone number
     * @param company the new company name
     * @param notes the new notes
     * @return the updated Contact entity
     * @throws IllegalArgumentException if contact not found or doesn't belong to user
     */
    @Transactional
    public Contact updateContact(Long contactId, Long userId, String email, 
                                  String displayName, String phone, String company, String notes) {
        Contact contact = contactRepo.findByIdAndUserId(contactId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Contact not found"));

        contact.setEmail(email);
        contact.setDisplayName(displayName);
        contact.setPhone(phone);
        contact.setCompany(company);
        contact.setNotes(notes);

        return contactRepo.save(contact);
    }

    /**
     * Deletes a contact.
     * 
     * @param contactId the ID of the contact to delete
     * @param userId the ID of the contact owner (for authorization)
     * @throws IllegalArgumentException if contact not found or doesn't belong to user
     */
    @Transactional
    public void deleteContact(Long contactId, Long userId) {
        Contact contact = contactRepo.findByIdAndUserId(contactId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Contact not found"));
        contactRepo.delete(contact);
    }

    /**
     * Toggles the favorite status of a contact.
     * 
     * @param contactId the ID of the contact
     * @param userId the ID of the contact owner
     * @return the new favorite status (true if now a favorite, false otherwise)
     * @throws IllegalArgumentException if contact not found or doesn't belong to user
     */
    @Transactional
    public boolean toggleFavorite(Long contactId, Long userId) {
        Contact contact = contactRepo.findByIdAndUserId(contactId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Contact not found"));
        
        contact.setFavorite(!contact.isFavorite());
        contactRepo.save(contact);
        return contact.isFavorite();
    }

    /**
     * Automatically adds a contact when sending email to a new recipient.
     * <p>
     * This method is called during email composition to auto-populate the
     * user's contact list. If the contact already exists, no action is taken.
     * </p>
     * 
     * @param userId the ID of the user sending the email
     * @param email the email address of the new recipient
     * @param displayName the display name to use for the contact
     */
    @Transactional
    public void autoAddContact(Long userId, String email, String displayName) {
        if (!contactRepo.existsByUserIdAndEmailIgnoreCase(userId, email)) {
            User user = userRepo.findById(userId).orElse(null);
            if (user != null) {
                Contact contact = new Contact(user, email, displayName);
                contactRepo.save(contact);
            }
        }
    }
}

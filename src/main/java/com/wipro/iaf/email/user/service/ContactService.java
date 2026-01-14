package com.wipro.iaf.email.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wipro.iaf.email.user.entity.Contact;
import com.wipro.iaf.email.user.entity.User;
import com.wipro.iaf.email.user.repo.ContactRepository;
import com.wipro.iaf.email.user.repo.UserRepository;

@Service
public class ContactService {

    private final ContactRepository contactRepo;
    private final UserRepository userRepo;

    public ContactService(ContactRepository contactRepo, UserRepository userRepo) {
        this.contactRepo = contactRepo;
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public List<Contact> getAllContacts(Long userId) {
        return contactRepo.findByUserIdOrderByDisplayNameAsc(userId);
    }

    @Transactional(readOnly = true)
    public List<Contact> getFavoriteContacts(Long userId) {
        return contactRepo.findByUserIdAndFavoriteTrue(userId);
    }

    @Transactional(readOnly = true)
    public List<Contact> searchContacts(Long userId, String query) {
        return contactRepo.searchContacts(userId, query);
    }

    @Transactional(readOnly = true)
    public Optional<Contact> getContact(Long contactId, Long userId) {
        return contactRepo.findByIdAndUserId(contactId, userId);
    }

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

    @Transactional
    public void deleteContact(Long contactId, Long userId) {
        Contact contact = contactRepo.findByIdAndUserId(contactId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Contact not found"));
        contactRepo.delete(contact);
    }

    @Transactional
    public boolean toggleFavorite(Long contactId, Long userId) {
        Contact contact = contactRepo.findByIdAndUserId(contactId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Contact not found"));
        
        contact.setFavorite(!contact.isFavorite());
        contactRepo.save(contact);
        return contact.isFavorite();
    }

    /**
     * Auto-add contact when sending email to new recipient
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

package com.wipro.iaf.email.user.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wipro.iaf.email.user.entity.Contact;

/**
 * Repository interface for {@link Contact} entity database operations.
 * 
 * <p>Provides methods for managing a user's personal address book including
 * creating, searching, and organizing contacts. All operations are scoped
 * to a specific user to maintain privacy between users.</p>
 * 
 * <p>Key features:
 * <ul>
 *   <li>Retrieve contacts sorted alphabetically by display name</li>
 *   <li>Filter favorite contacts for quick access</li>
 *   <li>Search contacts by email or display name</li>
 *   <li>Prevent duplicate contacts per user</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see Contact
 */
public interface ContactRepository extends JpaRepository<Contact, Long> {

    /**
     * Retrieves all contacts for a user sorted alphabetically by display name.
     * 
     * @param userId the ID of the user whose contacts to retrieve
     * @return list of contacts ordered by display name ascending
     */
    List<Contact> findByUserIdOrderByDisplayNameAsc(Long userId);

    /**
     * Retrieves only favorite contacts for a user.
     * 
     * <p>Favorites provide quick access to frequently used contacts.</p>
     * 
     * @param userId the ID of the user whose favorite contacts to retrieve
     * @return list of contacts marked as favorite
     */
    List<Contact> findByUserIdAndFavoriteTrue(Long userId);

    /**
     * Finds a specific contact by ID, ensuring it belongs to the specified user.
     * 
     * <p>The user ID check ensures users can only access their own contacts.</p>
     * 
     * @param id     the contact ID
     * @param userId the ID of the user who should own the contact
     * @return an {@link Optional} containing the contact if found and owned by user
     */
    Optional<Contact> findByIdAndUserId(Long id, Long userId);

    /**
     * Finds a contact by email address for a specific user (case-insensitive).
     * 
     * <p>Used to look up existing contacts when composing emails.</p>
     * 
     * @param userId the ID of the user
     * @param email  the email address to search for (case-insensitive)
     * @return an {@link Optional} containing the contact if found
     */
    Optional<Contact> findByUserIdAndEmailIgnoreCase(Long userId, String email);

    /**
     * Checks if a contact with the given email already exists for a user.
     * 
     * <p>Used to prevent duplicate contacts in a user's address book.</p>
     * 
     * @param userId the ID of the user
     * @param email  the email address to check (case-insensitive)
     * @return true if contact with this email exists for the user
     */
    boolean existsByUserIdAndEmailIgnoreCase(Long userId, String email);

    /**
     * Searches contacts by email or display name for autocomplete functionality.
     * 
     * <p>Performs case-insensitive partial matching on both email and display name.
     * Results are ordered with favorites first, then alphabetically by name.</p>
     * 
     * @param userId the ID of the user whose contacts to search
     * @param query  the search query to match against email and display name
     * @return list of matching contacts ordered by favorite status then name
     */
    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId " +
           "AND (LOWER(c.email) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "     OR LOWER(c.displayName) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY c.favorite DESC, c.displayName ASC")
    List<Contact> searchContacts(@Param("userId") Long userId, @Param("query") String query);

    /**
     * Deletes a contact by ID, ensuring it belongs to the specified user.
     * 
     * <p>The user ID check ensures users can only delete their own contacts.</p>
     * 
     * @param id     the contact ID to delete
     * @param userId the ID of the user who should own the contact
     */
    void deleteByIdAndUserId(Long id, Long userId);
}

package com.wipro.iaf.email.user.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wipro.iaf.email.user.entity.User;

/**
 * Repository interface for {@link User} entity database operations.
 * 
 * <p>Provides methods for querying and managing user accounts in the PulseMail system.
 * Extends {@link JpaRepository} for standard CRUD operations and adds custom
 * query methods for user-specific functionality.</p>
 * 
 * <p>Key features:
 * <ul>
 *   <li>User lookup by email address for authentication</li>
 *   <li>Email existence check for registration validation</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see User
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     * 
     * <p>Used primarily for authentication and user lookup during email operations.
     * Email matching is case-sensitive as stored in the database.</p>
     * 
     * @param email the email address to search for
     * @return an {@link Optional} containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user with the given email address already exists.
     * 
     * <p>Used during registration to prevent duplicate accounts.</p>
     * 
     * @param email the email address to check
     * @return true if a user with this email exists, false otherwise
     */
    boolean existsByEmail(String email);
}

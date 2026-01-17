package com.wipro.iaf.email.user.service;

import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wipro.iaf.email.user.dto.ChangePasswordRequest;
import com.wipro.iaf.email.user.dto.ProfileUpdateRequest;
import com.wipro.iaf.email.user.entity.User;
import com.wipro.iaf.email.user.repo.UserRepository;

/**
 * Service class for managing user profile operations in the PulseMail application.
 * <p>
 * This service provides functionality for:
 * <ul>
 *   <li>Retrieving user profile information</li>
 *   <li>Updating user display name and email signature</li>
 *   <li>Secure password change with complexity validation</li>
 * </ul>
 * </p>
 * <p>
 * All password operations use secure hashing via the configured {@link PasswordEncoder}.
 * </p>
 * 
 * <h3>Password Requirements:</h3>
 * <ul>
 *   <li>Minimum 8 characters</li>
 *   <li>At least one uppercase letter (A-Z)</li>
 *   <li>At least one lowercase letter (a-z)</li>
 *   <li>At least one digit (0-9)</li>
 *   <li>At least one special character (!@#$%^&*)</li>
 * </ul>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-16
 * @see User
 * @see ProfileUpdateRequest
 * @see ChangePasswordRequest
 */
@Service
public class ProfileService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Password complexity regex pattern.
     * Requires:
     * - At least 8 characters
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one digit
     * - At least one special character
     */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?]).{8,}$"
    );

    /**
     * Constructs a new ProfileService with required dependencies.
     * 
     * @param userRepo repository for user CRUD operations
     * @param passwordEncoder encoder for password hashing and verification
     */
    public ProfileService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves a user by their ID.
     * 
     * @param userId the ID of the user to retrieve
     * @return the User entity
     * @throws IllegalArgumentException if user is not found
     */
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepo.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    /**
     * Updates a user's profile information.
     * <p>
     * Updates the display name and/or email signature based on the provided request.
     * Null values in the request are ignored (partial update supported).
     * Display names are trimmed before saving.
     * </p>
     * 
     * @param userId the ID of the user to update
     * @param request the profile update request containing new values
     * @throws IllegalArgumentException if user is not found
     */
    @Transactional
    public void updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = getUserById(userId);
        
        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName().trim());
        }
        
        if (request.getSignature() != null) {
            user.setSignature(request.getSignature());
        }
        
        userRepo.save(user);
    }

    /**
     * Changes a user's password after validating the current password and complexity requirements.
     * <p>
     * This method performs the following validations:
     * <ul>
     *   <li>Verifies the current password matches</li>
     *   <li>Ensures new password meets complexity requirements</li>
     *   <li>Confirms new password matches the confirmation field</li>
     * </ul>
     * </p>
     * 
     * @param userId the ID of the user changing their password
     * @param request the password change request containing current and new passwords
     * @throws IllegalArgumentException if current password is incorrect,
     *         new password doesn't meet complexity requirements, or passwords don't match
     */
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = getUserById(userId);
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        // Validate new password complexity
        validatePasswordComplexity(request.getNewPassword());
        
        // Verify confirmation matches
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        
        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);
    }
    
    /**
     * Validates password complexity requirements.
     * 
     * @param password the password to validate
     * @throws IllegalArgumentException if password doesn't meet requirements
     */
    private void validatePasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException(
                "Password must contain at least one uppercase letter, " +
                "one lowercase letter, one digit, and one special character (!@#$%^&*)"
            );
        }
    }
    
    /**
     * Permanently deletes a user account after password verification.
     * <p>
     * This is a soft-delete operation. The user account is marked as deleted
     * but not removed from the database. This ensures:
     * <ul>
     *   <li>Recipients can still see emails from this user (shown as "[Deleted User]")</li>
     *   <li>Email threads and conversations remain intact</li>
     *   <li>The email address cannot be reused (prevents impersonation)</li>
     * </ul>
     * </p>
     * <p>
     * The following data is cleared/anonymized:
     * <ul>
     *   <li>Password hash (prevents login)</li>
     *   <li>Display name and signature</li>
     *   <li>Account is disabled</li>
     * </ul>
     * </p>
     * 
     * @param userId the ID of the user to delete
     * @param password the user's current password for verification
     * @throws IllegalArgumentException if password is incorrect
     */
    @Transactional
    public void deleteAccount(Long userId, String password) {
        User user = getUserById(userId);
        
        // Verify password before deletion
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Password is incorrect");
        }
        
        // Soft-delete: Mark as deleted and clear sensitive data
        user.setDeleted(true);
        user.setEnabled(false);
        user.setPasswordHash(""); // Clear password hash
        user.setDisplayName(null); // Clear display name
        user.setSignature(null);   // Clear signature
        
        userRepo.save(user);
    }
}

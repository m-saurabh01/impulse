package com.wipro.iaf.email.user.service;

import java.util.regex.Pattern;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wipro.iaf.email.user.entity.User;
import com.wipro.iaf.email.user.repo.UserRepository;

/**
 * Service class for managing user accounts in the PulseMail application.
 * <p>
 * This service handles core user management operations including:
 * <ul>
 *   <li>User registration with secure password hashing</li>
 *   <li>Email domain validation (must be @impulse.iaf.in)</li>
 *   <li>Password complexity validation</li>
 *   <li>Email uniqueness validation</li>
 *   <li>Default role assignment for new users</li>
 * </ul>
 * </p>
 * <p>
 * Passwords are securely hashed using the configured {@link PasswordEncoder}
 * before being stored in the database.
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
 * @see PasswordEncoder
 */
@Service
public class UserService {

	private final UserRepository userRepo;
	private final PasswordEncoder encoder;
	
	/** Required email domain for all users */
	@Value("${impulse.email.domain:@impulse.iaf.in}")
	private String requiredEmailDomain;

	/**
	 * Password complexity regex pattern.
	 * Requires:
	 * - At least 8 characters
	 * - At least one uppercase letter
	 * - At least one lowercase letter
	 * - At least one digit
	 * - At least one special character (!@#$%^&*()_+-=[]{}|;:,.<>?)
	 */
	private static final Pattern PASSWORD_PATTERN = Pattern.compile(
	    "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?]).{8,}$"
	);

	/**
	 * Constructs a new UserService with required dependencies.
	 * 
	 * @param userRepo repository for user CRUD operations
	 * @param encoder password encoder for secure password hashing
	 */
	public UserService(UserRepository userRepo, PasswordEncoder encoder) {
	    this.userRepo = userRepo;
	    this.encoder = encoder;
	}

	/**
	 * Registers a new user with the given email and password.
	 * <p>
	 * The password is securely hashed before storage. New users are
	 * assigned the default "USER" role.
	 * </p>
	 * 
	 * <p>Validation performed:</p>
	 * <ul>
	 *   <li>Email must end with configured domain (@impulse.iaf.in)</li>
	 *   <li>Email must not already be registered</li>
	 *   <li>Password must meet complexity requirements</li>
	 * </ul>
	 * 
	 * @param email the email address for the new user account
	 * @param rawPassword the plain text password (will be hashed)
	 * @throws IllegalArgumentException if email domain is invalid
	 * @throws IllegalArgumentException if email is already registered
	 * @throws IllegalArgumentException if password doesn't meet complexity requirements
	 */
    @Transactional
    public void register(String email, String rawPassword) {
        
        // Validate email domain
        if (email == null || !email.toLowerCase().endsWith(requiredEmailDomain.toLowerCase())) {
            throw new IllegalArgumentException(
                "Email must end with " + requiredEmailDomain + " (e.g., yourname" + requiredEmailDomain + ")"
            );
        }

        // Validate email uniqueness
        if (userRepo.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        // Validate password complexity
        validatePassword(rawPassword);

        User user = new User();
        user.setEmail(email.toLowerCase()); // Normalize email to lowercase
        user.setPasswordHash(encoder.encode(rawPassword));
        user.setRole("USER");

        userRepo.save(user);
    }
    
    /**
     * Validates password complexity requirements.
     * 
     * @param password the password to validate
     * @throws IllegalArgumentException if password doesn't meet requirements
     */
    public void validatePassword(String password) {
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
     * Gets the required email domain for user registration.
     * 
     * @return the required email domain (e.g., "@impulse.iaf.in")
     */
    public String getRequiredEmailDomain() {
        return requiredEmailDomain;
    }
}

package com.wipro.iaf.email.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wipro.iaf.email.user.dto.ChangePasswordRequest;
import com.wipro.iaf.email.user.dto.ProfileUpdateRequest;
import com.wipro.iaf.email.user.entity.User;
import com.wipro.iaf.email.user.repo.UserRepository;

@Service
public class ProfileService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public ProfileService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepo.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    /**
     * Update user profile (display name, signature)
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
     * Change user password
     */
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = getUserById(userId);
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        // Validate new password
        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters");
        }
        
        // Verify confirmation matches
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        
        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);
    }
}

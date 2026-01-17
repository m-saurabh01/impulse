package com.wipro.iaf.email.mail.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wipro.iaf.email.mail.entity.Email;
import com.wipro.iaf.email.mail.entity.EmailLabel;
import com.wipro.iaf.email.mail.entity.Label;
import com.wipro.iaf.email.mail.repo.EmailLabelRepository;
import com.wipro.iaf.email.mail.repo.EmailRepository;
import com.wipro.iaf.email.mail.repo.LabelRepository;
import com.wipro.iaf.email.user.entity.User;
import com.wipro.iaf.email.user.repo.UserRepository;
import com.wipro.iaf.email.user.service.AchievementService;

/**
 * Service class for managing email labels in the PulseMail application.
 * <p>
 * Labels allow users to organize and categorize their emails. This service provides:
 * <ul>
 *   <li>CRUD operations for user-defined labels with custom names and colors</li>
 *   <li>Assigning and removing labels from emails</li>
 *   <li>Retrieving emails by label for filtered views</li>
 *   <li>Integration with the achievement system for label-related achievements</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-16
 * @see Label
 * @see EmailLabel
 * @see AchievementService
 */
@Service
public class LabelService {

    private final LabelRepository labelRepo;
    private final EmailLabelRepository emailLabelRepo;
    private final EmailRepository emailRepo;
    private final UserRepository userRepo;
    private final AchievementService achievementService;

    /**
     * Constructs a new LabelService with required dependencies.
     * 
     * @param labelRepo repository for label CRUD operations
     * @param emailLabelRepo repository for email-label associations
     * @param emailRepo repository for email operations
     * @param userRepo repository for user lookups
     * @param achievementService service for tracking achievements
     */
    public LabelService(LabelRepository labelRepo, 
                        EmailLabelRepository emailLabelRepo,
                        EmailRepository emailRepo,
                        UserRepository userRepo,
                        AchievementService achievementService) {
        this.labelRepo = labelRepo;
        this.emailLabelRepo = emailLabelRepo;
        this.emailRepo = emailRepo;
        this.userRepo = userRepo;
        this.achievementService = achievementService;
    }

    /**
     * Retrieves all labels for a specific user, ordered alphabetically by name.
     * 
     * @param userId the ID of the user whose labels to retrieve
     * @return list of labels belonging to the user, sorted by name
     */
    @Transactional(readOnly = true)
    public List<Label> getLabelsForUser(Long userId) {
        return labelRepo.findByUserIdOrderByNameAsc(userId);
    }

    /**
     * Creates a new label for a user.
     * <p>
     * After successful creation, checks for label-related achievements.
     * </p>
     * 
     * @param userId the ID of the user creating the label
     * @param name the name for the new label
     * @param color the color code for the label (e.g., "#FF5733")
     * @return the newly created Label entity
     * @throws IllegalArgumentException if user not found or label name already exists
     */
    @Transactional
    public Label createLabel(Long userId, String name, String color) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (labelRepo.existsByUserIdAndNameIgnoreCase(userId, name)) {
            throw new IllegalArgumentException("Label with this name already exists");
        }

        Label label = new Label(user, name, color);
        Label savedLabel = labelRepo.save(label);
        
        // Check for label-related achievements
        achievementService.checkAndAwardAchievements(userId);
        
        return savedLabel;
    }

    /**
     * Updates an existing label's name and color.
     * 
     * @param labelId the ID of the label to update
     * @param userId the ID of the label owner (for authorization)
     * @param name the new name for the label
     * @param color the new color code for the label
     * @return the updated Label entity
     * @throws IllegalArgumentException if label not found or doesn't belong to user
     */
    @Transactional
    public Label updateLabel(Long labelId, Long userId, String name, String color) {
        Label label = labelRepo.findById(labelId)
            .filter(l -> l.getUser().getId().equals(userId))
            .orElseThrow(() -> new IllegalArgumentException("Label not found"));

        label.setName(name);
        label.setColor(color);
        return labelRepo.save(label);
    }

    /**
     * Deletes a label and all its email associations.
     * <p>
     * Removes all email-label associations before deleting the label itself.
     * </p>
     * 
     * @param labelId the ID of the label to delete
     * @param userId the ID of the label owner (for authorization)
     * @throws IllegalArgumentException if label not found or doesn't belong to user
     */
    @Transactional
    public void deleteLabel(Long labelId, Long userId) {
        Label label = labelRepo.findById(labelId)
            .filter(l -> l.getUser().getId().equals(userId))
            .orElseThrow(() -> new IllegalArgumentException("Label not found"));

        // Remove all email-label associations first
        emailLabelRepo.deleteByLabelId(labelId);
        labelRepo.delete(label);
    }

    /**
     * Retrieves all labels assigned to a specific email for a user.
     * 
     * @param emailId the ID of the email
     * @param userId the ID of the user (for filtering user-specific labels)
     * @return list of EmailLabel associations for the email
     */
    @Transactional(readOnly = true)
    public List<EmailLabel> getLabelsForEmail(Long emailId, Long userId) {
        return emailLabelRepo.findLabelsForEmail(emailId, userId);
    }

    /**
     * Adds a label to an email.
     * <p>
     * If the label is already assigned to the email, this method does nothing.
     * </p>
     * 
     * @param emailId the ID of the email to label
     * @param labelId the ID of the label to assign
     * @param userId the ID of the user (for authorization)
     * @throws IllegalArgumentException if email or label not found
     */
    @Transactional
    public void addLabelToEmail(Long emailId, Long labelId, Long userId) {
        if (emailLabelRepo.existsByEmailIdAndLabelIdAndUserId(emailId, labelId, userId)) {
            return; // Already has this label
        }

        Email email = emailRepo.findById(emailId)
            .orElseThrow(() -> new IllegalArgumentException("Email not found"));

        Label label = labelRepo.findById(labelId)
            .filter(l -> l.getUser().getId().equals(userId))
            .orElseThrow(() -> new IllegalArgumentException("Label not found"));

        EmailLabel emailLabel = new EmailLabel(email, label, userId);
        emailLabelRepo.save(emailLabel);
    }

    /**
     * Removes a label from an email.
     * 
     * @param emailId the ID of the email
     * @param labelId the ID of the label to remove
     * @param userId the ID of the user (for authorization)
     */
    @Transactional
    public void removeLabelFromEmail(Long emailId, Long labelId, Long userId) {
        emailLabelRepo.removeLabel(emailId, labelId, userId);
    }

    /**
     * Retrieves all emails with a specific label for a user.
     * 
     * @param labelId the ID of the label to filter by
     * @param userId the ID of the user
     * @param pageable pagination and sorting parameters
     * @return a page of EmailLabel associations for the label
     */
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<EmailLabel> getEmailsByLabel(Long labelId, Long userId, org.springframework.data.domain.Pageable pageable) {
        return emailLabelRepo.findEmailsByLabel(labelId, userId, pageable);
    }

    /**
     * Retrieves a label by its ID for a specific user.
     * 
     * @param labelId the ID of the label to retrieve
     * @param userId the ID of the label owner
     * @return the Label if found and owned by user, null otherwise
     */
    @Transactional(readOnly = true)
    public Label getLabelById(Long labelId, Long userId) {
        return labelRepo.findById(labelId)
            .filter(l -> l.getUser().getId().equals(userId))
            .orElse(null);
    }

    /**
     * Counts the number of emails with a specific label for a user.
     * 
     * @param labelId the ID of the label
     * @param userId the ID of the user
     * @return the count of emails with the specified label
     */
    @Transactional(readOnly = true)
    public long getEmailCountForLabel(Long labelId, Long userId) {
        return emailLabelRepo.countByLabelIdAndUserId(labelId, userId);
    }
}

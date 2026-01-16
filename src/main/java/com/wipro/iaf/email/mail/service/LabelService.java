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

@Service
public class LabelService {

    private final LabelRepository labelRepo;
    private final EmailLabelRepository emailLabelRepo;
    private final EmailRepository emailRepo;
    private final UserRepository userRepo;
    private final AchievementService achievementService;

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

    @Transactional(readOnly = true)
    public List<Label> getLabelsForUser(Long userId) {
        return labelRepo.findByUserIdOrderByNameAsc(userId);
    }

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

    @Transactional
    public Label updateLabel(Long labelId, Long userId, String name, String color) {
        Label label = labelRepo.findById(labelId)
            .filter(l -> l.getUser().getId().equals(userId))
            .orElseThrow(() -> new IllegalArgumentException("Label not found"));

        label.setName(name);
        label.setColor(color);
        return labelRepo.save(label);
    }

    @Transactional
    public void deleteLabel(Long labelId, Long userId) {
        Label label = labelRepo.findById(labelId)
            .filter(l -> l.getUser().getId().equals(userId))
            .orElseThrow(() -> new IllegalArgumentException("Label not found"));

        // Remove all email-label associations first
        emailLabelRepo.deleteByLabelId(labelId);
        labelRepo.delete(label);
    }

    @Transactional(readOnly = true)
    public List<EmailLabel> getLabelsForEmail(Long emailId, Long userId) {
        return emailLabelRepo.findLabelsForEmail(emailId, userId);
    }

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

    @Transactional
    public void removeLabelFromEmail(Long emailId, Long labelId, Long userId) {
        emailLabelRepo.removeLabel(emailId, labelId, userId);
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<EmailLabel> getEmailsByLabel(Long labelId, Long userId, org.springframework.data.domain.Pageable pageable) {
        return emailLabelRepo.findEmailsByLabel(labelId, userId, pageable);
    }

    @Transactional(readOnly = true)
    public Label getLabelById(Long labelId, Long userId) {
        return labelRepo.findById(labelId)
            .filter(l -> l.getUser().getId().equals(userId))
            .orElse(null);
    }

    @Transactional(readOnly = true)
    public long getEmailCountForLabel(Long labelId, Long userId) {
        return emailLabelRepo.countByLabelIdAndUserId(labelId, userId);
    }
}

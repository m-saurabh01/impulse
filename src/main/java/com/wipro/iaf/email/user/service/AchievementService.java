package com.wipro.iaf.email.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wipro.iaf.email.mail.repo.EmailRepository;
import com.wipro.iaf.email.mail.repo.EmailRecipientRepository;
import com.wipro.iaf.email.mail.repo.LabelRepository;
import com.wipro.iaf.email.user.entity.Achievement;
import com.wipro.iaf.email.user.entity.Achievement.AchievementCategory;
import com.wipro.iaf.email.user.entity.User;
import com.wipro.iaf.email.user.entity.UserAchievement;
import com.wipro.iaf.email.user.repo.AchievementRepository;
import com.wipro.iaf.email.user.repo.UserAchievementRepository;
import com.wipro.iaf.email.user.repo.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AchievementService {

    private final AchievementRepository achievementRepo;
    private final UserAchievementRepository userAchievementRepo;
    private final EmailRepository emailRepo;
    private final EmailRecipientRepository recipientRepo;
    private final LabelRepository labelRepo;
    private final UserRepository userRepo;

    public AchievementService(AchievementRepository achievementRepo, 
                              UserAchievementRepository userAchievementRepo,
                              EmailRepository emailRepo,
                              EmailRecipientRepository recipientRepo,
                              LabelRepository labelRepo,
                              UserRepository userRepo) {
        this.achievementRepo = achievementRepo;
        this.userAchievementRepo = userAchievementRepo;
        this.emailRepo = emailRepo;
        this.recipientRepo = recipientRepo;
        this.labelRepo = labelRepo;
        this.userRepo = userRepo;
    }

    /**
     * Initialize default achievements if they don't exist.
     */
    @PostConstruct
    @Transactional
    public void initializeAchievements() {
        if (achievementRepo.count() > 0) {
            return;
        }

        List<Achievement> achievements = new ArrayList<>();
        int order = 1;

        // Sending achievements
        achievements.add(new Achievement("FIRST_EMAIL", "First Steps", 
                "Send your first email", "bi-envelope-check", "#00b894", 
                AchievementCategory.SENDING, 1, 10));
        achievements.add(new Achievement("SENDER_10", "Getting Started", 
                "Send 10 emails", "bi-send", "#0984e3", 
                AchievementCategory.SENDING, 10, 25));
        achievements.add(new Achievement("SENDER_50", "Communicator", 
                "Send 50 emails", "bi-send-fill", "#6c5ce7", 
                AchievementCategory.SENDING, 50, 50));
        achievements.add(new Achievement("SENDER_100", "Power Sender", 
                "Send 100 emails", "bi-lightning", "#fdcb6e", 
                AchievementCategory.SENDING, 100, 100));
        achievements.add(new Achievement("SENDER_500", "Mail Master", 
                "Send 500 emails", "bi-trophy", "#f39c12", 
                AchievementCategory.SENDING, 500, 250));

        // Organizing achievements
        achievements.add(new Achievement("LABEL_CREATOR", "Organized", 
                "Create your first label", "bi-tag", "#00cec9", 
                AchievementCategory.ORGANIZING, 1, 15));
        achievements.add(new Achievement("LABEL_5", "Super Organized", 
                "Create 5 labels", "bi-tags", "#74b9ff", 
                AchievementCategory.ORGANIZING, 5, 40));
        achievements.add(new Achievement("INBOX_ZERO", "Inbox Zero", 
                "Have no unread emails", "bi-inbox", "#55efc4", 
                AchievementCategory.ORGANIZING, 0, 75));

        // Engagement achievements
        achievements.add(new Achievement("STARGAZER", "Stargazer", 
                "Star 10 emails", "bi-star-fill", "#ffeaa7", 
                AchievementCategory.ENGAGEMENT, 10, 20));
        achievements.add(new Achievement("CONTACTS_10", "Social Butterfly", 
                "Add 10 contacts", "bi-people", "#fd79a8", 
                AchievementCategory.ENGAGEMENT, 10, 30));

        // Special achievements
        achievements.add(new Achievement("EXPLORER", "Explorer", 
                "Discover a hidden feature", "bi-compass", "#a29bfe", 
                AchievementCategory.SPECIAL, 1, 100));
        achievements.add(new Achievement("FEEDBACK_GIVEN", "Feedback Friend", 
                "Submit your first feedback", "bi-chat-heart", "#e84393", 
                AchievementCategory.SPECIAL, 1, 25));
        achievements.add(new Achievement("EARLY_ADOPTER", "Early Adopter", 
                "Be among the first users", "bi-rocket-takeoff", "#e17055", 
                AchievementCategory.SPECIAL, 1, 50));

        for (Achievement a : achievements) {
            a.setSortOrder(order++);
        }

        achievementRepo.saveAll(achievements);
        log.info("Initialized {} achievements", achievements.size());
    }

    /**
     * Get all achievements with user's unlock status.
     */
    public List<AchievementWithStatus> getAchievementsForUser(Long userId) {
        List<Achievement> allAchievements = achievementRepo.findAllByOrderBySortOrderAsc();
        List<UserAchievement> userAchievements = userAchievementRepo.findByUserIdWithAchievements(userId);
        
        List<AchievementWithStatus> result = new ArrayList<>();
        
        for (Achievement a : allAchievements) {
            boolean unlocked = userAchievements.stream()
                    .anyMatch(ua -> ua.getAchievement().getId().equals(a.getId()));
            result.add(new AchievementWithStatus(a, unlocked));
        }
        
        return result;
    }

    /**
     * Alias for getAchievementsForUser - for controller compatibility.
     */
    public List<AchievementWithStatus> getAchievementsWithStatus(Long userId) {
        return getAchievementsForUser(userId);
    }

    /**
     * Get user's unlocked achievements.
     */
    public List<UserAchievement> getUnlockedAchievements(Long userId) {
        return userAchievementRepo.findByUserIdWithAchievements(userId);
    }

    /**
     * Alias for getUnlockedAchievements - for controller compatibility.
     */
    public List<UserAchievement> getUserAchievements(Long userId) {
        return getUnlockedAchievements(userId);
    }

    /**
     * Get user's total achievement points.
     */
    public int getTotalPoints(Long userId) {
        Integer points = userAchievementRepo.getTotalPointsByUserId(userId);
        return points != null ? points : 0;
    }

    /**
     * Get count of unlocked achievements.
     */
    public int getUnlockedCount(Long userId) {
        return userAchievementRepo.countByUserId(userId);
    }

    /**
     * Get total available achievements.
     */
    public long getTotalAchievements() {
        return achievementRepo.count();
    }

    /**
     * Get unnotified achievements (for showing popups).
     */
    public List<UserAchievement> getUnnotifiedAchievements(Long userId) {
        return userAchievementRepo.findUnnotifiedByUserId(userId);
    }

    /**
     * Mark all achievements as notified.
     */
    @Transactional
    public void markAchievementsNotified(Long userId) {
        userAchievementRepo.markAllNotified(userId);
    }

    /**
     * Check and award achievements asynchronously.
     * Called after email send, label creation, etc.
     */
    @Async
    @Transactional
    public void checkAndAwardAchievements(Long userId, AchievementCategory category) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) return;

        switch (category) {
            case SENDING:
                checkSendingAchievements(user);
                break;
            case ORGANIZING:
                checkOrganizingAchievements(user);
                break;
            case ENGAGEMENT:
                checkEngagementAchievements(user);
                break;
            default:
                break;
        }
    }

    /**
     * Check all achievement categories.
     * Called when we want a full check.
     */
    @Async
    @Transactional
    public void checkAndAwardAchievements(Long userId) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) return;

        checkSendingAchievements(user);
        checkOrganizingAchievements(user);
        checkEngagementAchievements(user);
    }

    /**
     * Award a specific achievement by code.
     */
    @Transactional
    public boolean awardAchievement(Long userId, String achievementCode) {
        if (userAchievementRepo.existsByUserIdAndAchievementCode(userId, achievementCode)) {
            return false;  // Already has this achievement
        }

        Optional<Achievement> achievement = achievementRepo.findByCode(achievementCode);
        User user = userRepo.findById(userId).orElse(null);
        
        if (achievement.isPresent() && user != null) {
            UserAchievement ua = new UserAchievement(user, achievement.get());
            userAchievementRepo.save(ua);
            log.info("Awarded achievement {} to user {}", achievementCode, userId);
            return true;
        }
        return false;
    }

    private void checkSendingAchievements(User user) {
        long sentCount = emailRepo.countBySenderIdAndDraftFalse(user.getId());
        
        if (sentCount >= 1) awardAchievement(user.getId(), "FIRST_EMAIL");
        if (sentCount >= 10) awardAchievement(user.getId(), "SENDER_10");
        if (sentCount >= 50) awardAchievement(user.getId(), "SENDER_50");
        if (sentCount >= 100) awardAchievement(user.getId(), "SENDER_100");
        if (sentCount >= 500) awardAchievement(user.getId(), "SENDER_500");
    }

    private void checkOrganizingAchievements(User user) {
        long labelCount = labelRepo.countByUserId(user.getId());
        
        if (labelCount >= 1) awardAchievement(user.getId(), "LABEL_CREATOR");
        if (labelCount >= 5) awardAchievement(user.getId(), "LABEL_5");
        
        // Check inbox zero
        long unreadCount = recipientRepo.countUnreadInbox(user.getId());
        if (unreadCount == 0) awardAchievement(user.getId(), "INBOX_ZERO");
    }

    private void checkEngagementAchievements(User user) {
        long starredCount = recipientRepo.countStarredByUserId(user.getId());
        if (starredCount >= 10) awardAchievement(user.getId(), "STARGAZER");
    }

    /**
     * DTO for achievement with unlock status
     */
    public static class AchievementWithStatus {
        public final Achievement achievement;
        public final boolean unlocked;

        public AchievementWithStatus(Achievement achievement, boolean unlocked) {
            this.achievement = achievement;
            this.unlocked = unlocked;
        }
    }
}

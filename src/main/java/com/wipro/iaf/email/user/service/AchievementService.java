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

/**
 * Service class for managing the gamification achievement system in the PulseMail application.
 * <p>
 * This service provides a comprehensive achievement/badge system that rewards users for
 * various activities within the application. The system includes:
 * <ul>
 *   <li>Automatic initialization of default achievements on startup</li>
 *   <li>Achievement categories: SENDING, ORGANIZING, ENGAGEMENT, and SPECIAL</li>
 *   <li>Progress tracking and automatic achievement unlocking</li>
 *   <li>Points system with per-achievement point values</li>
 *   <li>Notification system for newly unlocked achievements</li>
 * </ul>
 * </p>
 * <p>
 * Achievements are checked asynchronously after relevant user actions to avoid
 * impacting response times.
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-16
 * @see Achievement
 * @see UserAchievement
 * @see AchievementCategory
 */
@Service
@Slf4j
public class AchievementService {

    private final AchievementRepository achievementRepo;
    private final UserAchievementRepository userAchievementRepo;
    private final EmailRepository emailRepo;
    private final EmailRecipientRepository recipientRepo;
    private final LabelRepository labelRepo;
    private final UserRepository userRepo;

    /**
     * Constructs a new AchievementService with all required dependencies.
     * 
     * @param achievementRepo repository for achievement definitions
     * @param userAchievementRepo repository for user-achievement mappings
     * @param emailRepo repository for email statistics
     * @param recipientRepo repository for email recipient statistics
     * @param labelRepo repository for label statistics
     * @param userRepo repository for user lookups
     */
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
     * Initializes the default set of achievements in the database.
     * <p>
     * This method is called automatically on application startup via {@link PostConstruct}.
     * It creates the standard achievement set across all categories:
     * <ul>
     *   <li>SENDING: First email, 10/50/100/500 emails sent milestones</li>
     *   <li>ORGANIZING: Label creation milestones, Inbox Zero</li>
     *   <li>ENGAGEMENT: Starring emails, adding contacts</li>
     *   <li>SPECIAL: Explorer, Feedback, Early Adopter badges</li>
     * </ul>
     * </p>
     * <p>
     * If achievements already exist in the database, this method does nothing.
     * </p>
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
        achievements.add(new Achievement("EASTER_EGG_HUNTER", "Easter Egg Hunter", 
                "Find all 4 hidden easter eggs", "bi-egg", "#ff6b6b", 
                AchievementCategory.SPECIAL, 4, 200));

        for (Achievement a : achievements) {
            a.setSortOrder(order++);
        }

        achievementRepo.saveAll(achievements);
        log.info("Initialized {} achievements", achievements.size());
    }

    /**
     * Retrieves all achievements with their unlock status for a specific user.
     * <p>
     * Returns all available achievements in the system, indicating which ones
     * the user has unlocked.
     * </p>
     * 
     * @param userId the ID of the user to check achievements for
     * @return list of AchievementWithStatus objects containing achievement details and unlock status
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
     * Retrieves all achievements with their unlock status for a user.
     * <p>
     * This is an alias for {@link #getAchievementsForUser(Long)} provided
     * for controller compatibility.
     * </p>
     * 
     * @param userId the ID of the user
     * @return list of AchievementWithStatus objects
     */
    public List<AchievementWithStatus> getAchievementsWithStatus(Long userId) {
        return getAchievementsForUser(userId);
    }

    /**
     * Retrieves all achievements that a user has unlocked.
     * 
     * @param userId the ID of the user
     * @return list of UserAchievement records with achievement details eagerly loaded
     */
    public List<UserAchievement> getUnlockedAchievements(Long userId) {
        return userAchievementRepo.findByUserIdWithAchievements(userId);
    }

    /**
     * Retrieves all achievements unlocked by a user.
     * <p>
     * This is an alias for {@link #getUnlockedAchievements(Long)} provided
     * for controller compatibility.
     * </p>
     * 
     * @param userId the ID of the user
     * @return list of UserAchievement records
     */
    public List<UserAchievement> getUserAchievements(Long userId) {
        return getUnlockedAchievements(userId);
    }

    /**
     * Calculates the total achievement points earned by a user.
     * 
     * @param userId the ID of the user
     * @return the sum of points from all unlocked achievements, or 0 if none
     */
    public int getTotalPoints(Long userId) {
        Integer points = userAchievementRepo.getTotalPointsByUserId(userId);
        return points != null ? points : 0;
    }

    /**
     * Counts the number of achievements unlocked by a user.
     * 
     * @param userId the ID of the user
     * @return the count of unlocked achievements
     */
    public int getUnlockedCount(Long userId) {
        return userAchievementRepo.countByUserId(userId);
    }

    /**
     * Gets the total number of achievements available in the system.
     * 
     * @return the total count of all defined achievements
     */
    public long getTotalAchievements() {
        return achievementRepo.count();
    }

    /**
     * Retrieves achievements that have been unlocked but not yet shown to the user.
     * <p>
     * Used for displaying achievement popup notifications.
     * </p>
     * 
     * @param userId the ID of the user
     * @return list of UserAchievement records that haven't been notified
     */
    public List<UserAchievement> getUnnotifiedAchievements(Long userId) {
        return userAchievementRepo.findUnnotifiedByUserId(userId);
    }

    /**
     * Marks all of a user's achievements as notified.
     * <p>
     * Called after displaying achievement notifications to prevent re-display.
     * </p>
     * 
     * @param userId the ID of the user
     */
    @Transactional
    public void markAchievementsNotified(Long userId) {
        userAchievementRepo.markAllNotified(userId);
    }

    /**
     * Checks and awards achievements for a specific category asynchronously.
     * <p>
     * This method runs asynchronously to avoid impacting user-facing response times.
     * It is called after relevant user actions like sending emails or creating labels.
     * </p>
     * 
     * @param userId the ID of the user to check achievements for
     * @param category the achievement category to check
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
     * Checks and awards achievements across all categories asynchronously.
     * <p>
     * Performs a comprehensive check of all achievement categories for the user.
     * This method runs asynchronously to avoid impacting response times.
     * </p>
     * 
     * @param userId the ID of the user to check achievements for
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
     * Awards a specific achievement to a user by achievement code.
     * <p>
     * If the user already has the achievement, this method returns false
     * and no duplicate is created.
     * </p>
     * 
     * @param userId the ID of the user to award the achievement to
     * @param achievementCode the unique code of the achievement (e.g., "FIRST_EMAIL")
     * @return true if the achievement was newly awarded, false if already unlocked or not found
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

    /**
     * Checks and awards sending-related achievements based on email count.
     * <p>
     * Awards: FIRST_EMAIL (1), SENDER_10 (10), SENDER_50 (50),
     * SENDER_100 (100), SENDER_500 (500) based on sent email count.
     * </p>
     * 
     * @param user the user to check achievements for
     */
    private void checkSendingAchievements(User user) {
        long sentCount = emailRepo.countBySenderIdAndDraftFalse(user.getId());
        
        if (sentCount >= 1) awardAchievement(user.getId(), "FIRST_EMAIL");
        if (sentCount >= 10) awardAchievement(user.getId(), "SENDER_10");
        if (sentCount >= 50) awardAchievement(user.getId(), "SENDER_50");
        if (sentCount >= 100) awardAchievement(user.getId(), "SENDER_100");
        if (sentCount >= 500) awardAchievement(user.getId(), "SENDER_500");
    }

    /**
     * Checks and awards organizing-related achievements.
     * <p>
     * Awards: LABEL_CREATOR (1 label), LABEL_5 (5 labels),
     * INBOX_ZERO (no unread emails).
     * </p>
     * 
     * @param user the user to check achievements for
     */
    private void checkOrganizingAchievements(User user) {
        long labelCount = labelRepo.countByUserId(user.getId());
        
        if (labelCount >= 1) awardAchievement(user.getId(), "LABEL_CREATOR");
        if (labelCount >= 5) awardAchievement(user.getId(), "LABEL_5");
        
        // Check inbox zero
        long unreadCount = recipientRepo.countUnreadInbox(user.getId());
        if (unreadCount == 0) awardAchievement(user.getId(), "INBOX_ZERO");
    }

    /**
     * Checks and awards engagement-related achievements.
     * <p>
     * Awards: STARGAZER (10 starred emails).
     * </p>
     * 
     * @param user the user to check achievements for
     */
    private void checkEngagementAchievements(User user) {
        long starredCount = recipientRepo.countStarredByUserId(user.getId());
        if (starredCount >= 10) awardAchievement(user.getId(), "STARGAZER");
    }

    /**
     * Data Transfer Object representing an achievement with its unlock status for a user.
     * <p>
     * Used to display achievements in the UI with clear indication of whether
     * the current user has unlocked each achievement.
     * </p>
     */
    public static class AchievementWithStatus {
        /** The achievement definition */
        public final Achievement achievement;
        /** Whether the achievement has been unlocked by the user */
        public final boolean unlocked;

        /**
         * Constructs a new AchievementWithStatus.
         * 
         * @param achievement the achievement definition
         * @param unlocked whether the achievement is unlocked
         */
        public AchievementWithStatus(Achievement achievement, boolean unlocked) {
            this.achievement = achievement;
            this.unlocked = unlocked;
        }
    }
}

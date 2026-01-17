package com.wipro.iaf.email.user.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wipro.iaf.email.user.entity.Achievement;

/**
 * Repository interface for {@link Achievement} entity database operations.
 * 
 * <p>Provides methods for querying achievement definitions. Achievements are
 * system-wide definitions that users can unlock by meeting certain criteria.
 * This repository manages the achievement templates, not user progress.</p>
 * 
 * <p>Key features:
 * <ul>
 *   <li>Lookup achievements by unique code for programmatic access</li>
 *   <li>Retrieve all achievements sorted for display in UI</li>
 *   <li>Filter achievements by category for organized presentation</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see Achievement
 * @see com.wipro.iaf.email.user.entity.UserAchievement
 */
@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    
    /**
     * Finds an achievement by its unique code identifier.
     * 
     * <p>Used for programmatic lookups when checking if a user qualifies
     * for a specific achievement (e.g., "FIRST_EMAIL", "INBOX_ZERO").</p>
     * 
     * @param code the unique code identifier of the achievement
     * @return an {@link Optional} containing the achievement if found
     */
    Optional<Achievement> findByCode(String code);
    
    /**
     * Retrieves all achievements sorted by their display order.
     * 
     * <p>Used to display all available achievements in the UI
     * in a consistent, predefined order.</p>
     * 
     * @return list of all achievements ordered by sortOrder ascending
     */
    List<Achievement> findAllByOrderBySortOrderAsc();
    
    /**
     * Retrieves achievements filtered by category, sorted by display order.
     * 
     * <p>Allows the UI to display achievements grouped by category
     * (SENDING, RECEIVING, ORGANIZING, ENGAGEMENT, SPECIAL).</p>
     * 
     * @param category the {@link Achievement.AchievementCategory} to filter by
     * @return list of achievements in the specified category, ordered by sortOrder
     */
    List<Achievement> findByCategoryOrderBySortOrderAsc(Achievement.AchievementCategory category);
}

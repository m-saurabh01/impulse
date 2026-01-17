package com.wipro.iaf.email.common.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wipro.iaf.email.common.entity.Feedback;
import com.wipro.iaf.email.common.entity.Feedback.FeedbackType;

/**
 * Repository interface for {@link Feedback} entity database operations.
 * 
 * <p>Provides methods for querying, counting, and managing user feedback entries.
 * Extends {@link JpaRepository} for standard CRUD operations and adds custom
 * query methods for feedback-specific functionality.</p>
 * 
 * <p>Key features:
 * <ul>
 *   <li>Paginated retrieval of all feedback sorted by creation date</li>
 *   <li>Filtering feedback by type (bug reports, suggestions, etc.)</li>
 *   <li>Counting unread feedback for notification badges</li>
 *   <li>Aggregating feedback counts by type for analytics</li>
 * </ul>
 * </p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see Feedback
 * @see FeedbackService
 */
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    /**
     * Retrieves all feedback entries with pagination, ordered by creation date (newest first).
     * 
     * @param pageable pagination parameters (page number, size, etc.)
     * @return a {@link Page} containing feedback entries for the requested page
     */
    Page<Feedback> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Retrieves feedback entries of a specific type with pagination.
     * 
     * @param type     the {@link FeedbackType} to filter by
     * @param pageable pagination parameters
     * @return a {@link Page} containing feedback entries of the specified type
     */
    Page<Feedback> findByTypeOrderByCreatedAtDesc(FeedbackType type, Pageable pageable);

    /**
     * Retrieves all unread feedback entries ordered by creation date.
     * 
     * <p>Used for administrative review of new feedback that hasn't been processed yet.</p>
     * 
     * @return a list of unread feedback entries, newest first
     */
    List<Feedback> findByReadFalseOrderByCreatedAtDesc();

    /**
     * Counts the total number of unread feedback entries.
     * 
     * <p>Used for displaying notification badges in the admin interface.</p>
     * 
     * @return the count of feedback entries where read = false
     */
    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.read = false")
    long countUnread();

    /**
     * Aggregates feedback counts grouped by type.
     * 
     * <p>Returns data suitable for analytics dashboards showing distribution
     * of feedback types (e.g., how many bug reports vs feature requests).</p>
     * 
     * @return a list of Object arrays where each array contains [FeedbackType, count]
     */
    @Query("SELECT f.type, COUNT(f) FROM Feedback f GROUP BY f.type")
    List<Object[]> countByType();
}

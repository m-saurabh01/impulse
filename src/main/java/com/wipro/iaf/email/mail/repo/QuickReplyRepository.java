package com.wipro.iaf.email.mail.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wipro.iaf.email.mail.entity.QuickReply;

/**
 * Repository interface for managing {@link QuickReply} entities.
 * <p>
 * This repository provides data access operations for user-defined quick reply templates.
 * Quick replies are pre-defined text snippets that users can insert into email compositions
 * for faster and more efficient email responses.
 * </p>
 *
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2025-01-16
 * @see QuickReply
 * @see JpaRepository
 */
public interface QuickReplyRepository extends JpaRepository<QuickReply, Long> {

    /**
     * Retrieves all quick replies for a specific user, ordered by sort order.
     * <p>
     * Returns quick replies sorted in ascending order based on the user's
     * preferred arrangement for display in the user interface.
     * </p>
     *
     * @param userId the unique identifier of the user
     * @return a {@link List} of quick replies owned by the user, sorted by sort order ascending
     */
    List<QuickReply> findByUserIdOrderBySortOrderAsc(Long userId);

    /**
     * Finds a specific quick reply by its ID, ensuring it belongs to the specified user.
     * <p>
     * This method provides secure access by requiring both the quick reply ID
     * and the user ID, preventing users from accessing others' quick replies.
     * </p>
     *
     * @param id the unique identifier of the quick reply
     * @param userId the unique identifier of the user
     * @return an {@link Optional} containing the quick reply if found and owned by the user, or empty otherwise
     */
    Optional<QuickReply> findByIdAndUserId(Long id, Long userId);

    /**
     * Deletes a quick reply by its ID, ensuring it belongs to the specified user.
     * <p>
     * This method provides secure deletion by requiring both the quick reply ID
     * and the user ID, preventing users from deleting quick replies they don't own.
     * </p>
     *
     * @param id the unique identifier of the quick reply to delete
     * @param userId the unique identifier of the user who owns the quick reply
     */
    void deleteByIdAndUserId(Long id, Long userId);

    /**
     * Counts the total number of quick replies created by a specific user.
     * <p>
     * This method is useful for enforcing limits on the number of quick replies
     * a user can create, or for tracking user activity and statistics.
     * </p>
     *
     * @param userId the unique identifier of the user
     * @return the count of quick replies created by the specified user
     */
    long countByUserId(Long userId);
}

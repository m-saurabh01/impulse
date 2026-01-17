package com.wipro.iaf.email.mail.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wipro.iaf.email.mail.entity.Label;

/**
 * Repository interface for managing {@link Label} entities.
 * <p>
 * This repository provides data access operations for user-defined email labels,
 * allowing users to organize and categorize their emails. Labels are user-specific
 * and can be applied to multiple emails for custom organization.
 * </p>
 *
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2025-01-16
 * @see Label
 * @see JpaRepository
 */
public interface LabelRepository extends JpaRepository<Label, Long> {

    /**
     * Retrieves all labels belonging to a specific user, ordered alphabetically.
     * <p>
     * Returns labels sorted by name in ascending order for consistent display
     * in the user interface.
     * </p>
     *
     * @param userId the unique identifier of the user
     * @return a {@link List} of labels owned by the user, sorted by name ascending
     */
    List<Label> findByUserIdOrderByNameAsc(Long userId);

    /**
     * Checks if a label with the specified name already exists for a user.
     * <p>
     * The comparison is case-insensitive to prevent duplicate labels with
     * different casing (e.g., "Work" and "work" are considered the same).
     * </p>
     *
     * @param userId the unique identifier of the user
     * @param name the label name to check for existence
     * @return {@code true} if a label with the given name exists for the user, {@code false} otherwise
     */
    boolean existsByUserIdAndNameIgnoreCase(Long userId, String name);

    /**
     * Deletes a label by its ID, ensuring it belongs to the specified user.
     * <p>
     * This method provides secure deletion by requiring both the label ID
     * and the user ID, preventing users from deleting labels they don't own.
     * </p>
     *
     * @param id the unique identifier of the label to delete
     * @param userId the unique identifier of the user who owns the label
     */
    void deleteByIdAndUserId(Long id, Long userId);
    
    /**
     * Counts the total number of labels created by a specific user.
     * <p>
     * This method is typically used for tracking user achievements and statistics,
     * providing insight into the user's organizational activity.
     * </p>
     *
     * @param userId the unique identifier of the user
     * @return the count of labels created by the specified user
     */
    long countByUserId(Long userId);
}

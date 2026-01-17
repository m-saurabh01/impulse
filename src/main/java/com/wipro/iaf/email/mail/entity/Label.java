package com.wipro.iaf.email.mail.entity;

import javax.persistence.*;

import com.wipro.iaf.email.user.entity.User;
import lombok.Data;

/**
 * Entity representing a user-defined label for organizing emails.
 * 
 * <p>Labels allow users to categorize and organize their emails with
 * custom tags. Each label has a name and color for visual distinction.
 * Labels are user-specific - each user has their own set of labels.</p>
 * 
 * <p>Emails can have multiple labels assigned via the {@link EmailLabel}
 * junction entity, allowing flexible multi-category organization.</p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see EmailLabel
 * @see User
 */
@Entity
@Table(name = "labels", indexes = {
    @Index(name = "idx_label_user", columnList = "user_id")
})
@Data
public class Label {

    /** Unique identifier for the label */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who owns this label */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /** Display name for the label (max 50 characters) */
    @Column(nullable = false, length = 50)
    private String name;

    /** Hex color code for visual representation (e.g., "#ff5733") */
    @Column(nullable = false, length = 7)
    private String color;

    /**
     * Default constructor required by JPA.
     */
    public Label() {}

    /**
     * Constructs a new Label with the specified attributes.
     * 
     * @param user  the user who owns this label
     * @param name  the display name for the label
     * @param color the hex color code (e.g., "#ff5733")
     */
    public Label(User user, String name, String color) {
        this.user = user;
        this.name = name;
        this.color = color;
    }
}

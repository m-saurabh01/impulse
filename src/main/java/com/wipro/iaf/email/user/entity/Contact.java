package com.wipro.iaf.email.user.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

import lombok.Data;

@Entity
@Table(name = "contacts", indexes = {
    @Index(name = "idx_contact_user", columnList = "user_id"),
    @Index(name = "idx_contact_email", columnList = "contact_email")
})
@Data
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "contact_email", nullable = false, length = 255)
    private String email;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String company;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_favorite")
    private boolean favorite;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Contact() {}

    public Contact(User user, String email, String displayName) {
        this.user = user;
        this.email = email;
        this.displayName = displayName;
    }

    /**
     * Get display name or email if name is not set
     */
    public String getDisplayNameOrEmail() {
        if (displayName != null && !displayName.trim().isEmpty()) {
            return displayName;
        }
        return email;
    }
}

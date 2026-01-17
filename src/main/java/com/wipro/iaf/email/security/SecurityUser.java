package com.wipro.iaf.email.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.wipro.iaf.email.user.entity.User;

/**
 * Spring Security UserDetails implementation for PulseMail.
 * 
 * <p>Adapts the application's {@link User} entity to Spring Security's
 * {@link UserDetails} interface. Provides authentication and authorization
 * information to the security framework.</p>
 * 
 * <p>This class is immutable and serializable for session storage.</p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see User
 * @see UserDetails
 */
public class SecurityUser implements UserDetails {

    private static final long serialVersionUID = 1L;
    
    /** The user's database ID */
    private final Long id;
    /** The user's email address (used as username) */
    private final String email;
    /** The user's hashed password */
    private final String password;
    /** The user's role (e.g., "USER", "ADMIN") */
    private final String role;
    /** The user's display name */
    private final String displayName;

    /**
     * Constructs a SecurityUser from a User entity.
     * 
     * @param user the User entity to adapt
     */
    public SecurityUser(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPasswordHash();
        this.role = user.getRole();
        this.displayName = user.getDisplayNameOrEmail();
    }

    /**
     * Gets the user's database ID.
     * 
     * @return the user ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the user's email address.
     * 
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the user's display name.
     * 
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the user's granted authorities.
     * 
     * <p>Returns a single authority with "ROLE_" prefix followed by the user's role.</p>
     * 
     * @return collection containing the user's role authority
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(
            new SimpleGrantedAuthority("ROLE_" + role)
        );
    }

    /** {@inheritDoc} */
    @Override public String getPassword() { return password; }
    /** {@inheritDoc} */
    @Override public String getUsername() { return email; }
    /** {@inheritDoc} */
    @Override public boolean isAccountNonExpired() { return true; }
    /** {@inheritDoc} */
    @Override public boolean isAccountNonLocked() { return true; }
    /** {@inheritDoc} */
    @Override public boolean isCredentialsNonExpired() { return true; }
    /** {@inheritDoc} */
    @Override public boolean isEnabled() { return true; }
}

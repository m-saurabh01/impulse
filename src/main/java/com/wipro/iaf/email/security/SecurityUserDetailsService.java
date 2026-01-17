package com.wipro.iaf.email.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.wipro.iaf.email.user.entity.User;
import com.wipro.iaf.email.user.repo.UserRepository;

/**
 * Spring Security UserDetailsService implementation for PulseMail.
 * 
 * <p>Loads user details from the database for authentication.
 * This service is used by Spring Security during the login process
 * to retrieve user credentials and authorities.</p>
 * 
 * @author Saurabh Mishra
 * @version 1.0
 * @since 2026-01-01
 * @see SecurityUser
 * @see UserDetailsService
 */
@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    /**
     * Constructs the SecurityUserDetailsService.
     * 
     * @param userRepo the user repository for database access
     */
    public SecurityUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Loads user details by email address.
     * 
     * <p>Called by Spring Security during authentication to retrieve
     * the user's credentials and authorities.</p>
     * 
     * @param email the user's email address
     * @return the UserDetails for the user
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(email));

        return new SecurityUser(user);
    }
}

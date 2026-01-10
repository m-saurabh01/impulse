package com.wipro.iaf.email.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.wipro.iaf.email.user.entity.User;
import com.wipro.iaf.email.user.repo.UserRepository;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    public SecurityUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(email));

        return new SecurityUser(user);
    }
}

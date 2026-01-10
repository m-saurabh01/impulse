package com.wipro.iaf.email.user.service;

import javax.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wipro.iaf.email.user.entity.User;
import com.wipro.iaf.email.user.repo.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepo;
	private final PasswordEncoder encoder;

	public UserService(UserRepository userRepo, PasswordEncoder encoder) {
	    this.userRepo = userRepo;
	    this.encoder = encoder;
	}

    @Transactional
    public void register(String email, String rawPassword) {

        if (userRepo.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(encoder.encode(rawPassword));
        user.setRole("USER");

        userRepo.save(user);
    }
}

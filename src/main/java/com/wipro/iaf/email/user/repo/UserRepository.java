package com.wipro.iaf.email.user.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wipro.iaf.email.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
